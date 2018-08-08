package com.testerum.service.step

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.*
import com.testerum.model.step.filter.StepsTreeFilter
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.RootStepNode
import com.testerum.model.step.tree.builder.StepTreeBuilder
import com.testerum.model.text.StepPattern
import com.testerum.model.util.StepHashUtil
import com.testerum.service.step.impl.BasicStepsService
import com.testerum.service.step.impl.ComposedStepsService
import com.testerum.service.step.impl.StepsResolver
import com.testerum.service.step.util.StepsFilterUtil
import com.testerum.service.step.util.getStepWithTheSameStepDef
import com.testerum.service.warning.WarningService
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

open class StepService(private val basicStepsService: BasicStepsService,
                       private val composedStepsService: ComposedStepsService,
                       private val warningService: WarningService) {

    @Volatile private var steps: MutableMap<String, StepDef> = hashMapOf()
    private val basicStepsMap: HashMap<String, BasicStepDef> = hashMapOf()

    private val stepsMapLoadersLatch = CountDownLatch(1)


    fun initInBackgroundThread() {
        object : Thread() {
            override fun run() {
                loadSteps()
            }
        }.start()
    }

    fun loadSteps() {
        val threadExecutor = Executors.newFixedThreadPool(2)
        try {
            val basicStepsLoaderFuture = threadExecutor.submit(
                    Callable { basicStepsService.getBasicSteps() }
            )
            val composedStepsLoaderFuture = threadExecutor.submit(
                    Callable { composedStepsService.getComposedSteps() }
            )

            val basicSteps = basicStepsLoaderFuture.get()

            basicStepsMap.clear()
            basicStepsMap.putAll(
                    basicSteps.associateBy({ StepHashUtil.calculateStepHash(it)}, {it})
            )

            val unresolvedComposedSteps = composedStepsLoaderFuture.get()

            reinitializeSteps(unresolvedComposedSteps)

            stepsMapLoadersLatch.countDown()
        } finally {
            // Shutdown forcefully (don't wait for executing thread). This is safe because:
            // - if there was no exception, at this point all threads have finished
            // - if there was an exception, we don't care
            threadExecutor.shutdownNow()
        }
    }

    private fun reinitializeSteps(unresolvedComposedSteps: List<ComposedStepDef>) {
        steps = StepsResolver(
                unresolvedComposedStepsMap = unresolvedComposedSteps.associateBy { StepHashUtil.calculateStepHash(it) },
                basicStepsMap = basicStepsMap
        ).resolve()
    }

    fun reinitializeComposedSteps() {
        val unresolvedComposedSteps = composedStepsService.getComposedSteps()

        reinitializeSteps(unresolvedComposedSteps)
    }

    private fun resolveComposedStep(unresolvedComposedStep: ComposedStepDef): ComposedStepDef {
        val resolvedStepCalls = mutableListOf<StepCall>()

        for (unresolvedStepCall in unresolvedComposedStep.stepCalls) {
            var resolvedStepDef = steps[StepHashUtil.calculateStepHash(unresolvedStepCall.stepDef)]

            if (resolvedStepDef == null) {
                resolvedStepDef = UndefinedStepDef(unresolvedComposedStep.phase, unresolvedComposedStep.stepPattern)
            }

            resolvedStepCalls.add(
                    StepCall(
                            unresolvedStepCall.id,
                            resolvedStepDef,
                            unresolvedStepCall.args
                    )
            )
        }

        return unresolvedComposedStep.copy(stepCalls = resolvedStepCalls)
    }

    fun getStepDefByPhaseAndPattern(stepPhase: StepPhaseEnum, stepPattern: StepPattern): StepDef {
        val stepHash = StepHashUtil.calculateStepHash(stepPhase, stepPattern)

        return steps[stepHash]
                ?: UndefinedStepDef(stepPhase, stepPattern)
    }

    fun getAllSteps(): List<StepDef> {
        return ArrayList(steps.values)
    }

    fun getBasicSteps(stepsTreeFilter: StepsTreeFilter = StepsTreeFilter()): List<BasicStepDef> {
        stepsMapLoadersLatch.await()

        val result = mutableListOf<BasicStepDef>()
        for (stepDef in steps.values) {
            if (stepDef is BasicStepDef && StepsFilterUtil.isStepMatchingFilter(stepDef, stepsTreeFilter)) {
                result.add(stepDef)
            }
        }

        return result
    }

    fun getComposedSteps(stepsTreeFilter: StepsTreeFilter = StepsTreeFilter()): List<ComposedStepDef> {
        stepsMapLoadersLatch.await()

        val result = mutableListOf<ComposedStepDef>()
        for (stepDef in steps.values) {
            if (stepDef is ComposedStepDef && StepsFilterUtil.isStepMatchingFilter(stepDef, stepsTreeFilter)) {
                result.add(stepDef)
            }
        }

        return result
    }

    fun getComposedStepByPath(searchedPath: Path): ComposedStepDef? {
        stepsMapLoadersLatch.await()

        val composedSteps = getComposedSteps()
        for (composedStep in composedSteps) {
            if (composedStep.path == searchedPath) {
                return warningService.composedStepWithWarnings(composedStep, keepExistingWarnings = true)
            }
        }

        return null
    }

    fun getBasicStepByPath(searchedPath: Path): BasicStepDef? {
        stepsMapLoadersLatch.await()

        val basicSteps = getBasicSteps()
        for (basicStep in basicSteps) {
            if (basicStep.path == searchedPath) {
                return basicStep
            }
        }
        return null
    }

    fun create(composedStepDef: ComposedStepDef): ComposedStepDef {
        stepsMapLoadersLatch.await()

        val stepDefWithCollision = getStepWithTheSameStepDef(composedStepDef, steps.values.toList())
        if (stepDefWithCollision != null) {
            val stepInCollisionType = if (stepDefWithCollision is BasicStepDef) "Basic Step" else "Composed FileStep"
            val stepInCollisionPath = if (stepDefWithCollision is BasicStepDef) {
                stepDefWithCollision.path.toString().replace("/", "") + "()"
            } else {
                "/"+stepDefWithCollision.path.toString()
            }
            val validationModel = ValidationModel(
                    "Another step with the same definition exists. \n" +
                            "The conflicting step has type [$stepInCollisionType] and is at path [$stepInCollisionPath]"
            )
            validationModel.fieldsWithValidationErrors["stepPattern"] = "step_pattern_already_exists"
            throw ValidationException(
                    validationModel
            )
        }

        val savedComposedStep = composedStepsService.create(composedStepDef)
        reinitializeComposedSteps()
        val resolvedSavedComposedStep = resolveComposedStep(savedComposedStep)

        return warningService.composedStepWithWarnings(resolvedSavedComposedStep, keepExistingWarnings = true)
    }

    fun update(composedStepDef: ComposedStepDef): ComposedStepDef {
        stepsMapLoadersLatch.await()

        val savedComposedStep = composedStepsService.update(composedStepDef)
        reinitializeComposedSteps()

        return resolveComposedStep(savedComposedStep)
    }

    fun remove(path: Path) {
        stepsMapLoadersLatch.await()

        val composedStepByPath = getComposedStepByPath(path)
        if (composedStepByPath != null) {
            setUnresolvedStepsCallsOf(composedStepByPath)
            steps.remove(StepHashUtil.calculateStepHash(composedStepByPath.phase, composedStepByPath.stepPattern))
        }
        composedStepsService.remove(path)

    }

    private fun setUnresolvedStepsCallsOf(stepDefToSetAsUnresolved: StepDef) {
        for (stepEntry in steps) {
            val stepDef = stepEntry.value as? ComposedStepDef
                    ?: continue

            var hasStepThatNeedsToBeSetAsUndefined = false
            val resolvedStepCalls = mutableListOf<StepCall>()

            for (stepCall in stepDef.stepCalls) {
                if (stepCall.stepDef == stepDefToSetAsUnresolved) {
                    val undefinedStepCall1 = StepCall(
                            stepCall.id,
                            UndefinedStepDef(stepDefToSetAsUnresolved.phase, stepDefToSetAsUnresolved.stepPattern),
                            stepCall.args
                    )
                    resolvedStepCalls.add(undefinedStepCall1)
                    hasStepThatNeedsToBeSetAsUndefined = true
                } else {
                    resolvedStepCalls.add(stepCall)
                }
            }

            if (hasStepThatNeedsToBeSetAsUndefined) {
                steps.replace(
                        stepEntry.key,
                        stepDef.copy(
                                stepCalls = resolvedStepCalls
                        )
                )
            }
        }
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        stepsMapLoadersLatch.await()

        val renamedPath = composedStepsService.renameDirectory(renamePath)

        reinitializeSteps(
                composedStepsService.getComposedSteps()
        )

        return renamedPath
    }

    fun deleteDirectory(pathToDelete: Path) {
        stepsMapLoadersLatch.await()

        composedStepsService.deleteDirectory(pathToDelete)

        reinitializeSteps(
                composedStepsService.getComposedSteps()
        )
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        stepsMapLoadersLatch.await()

        composedStepsService.moveDirectoryOrFile(copyPath)

        reinitializeSteps(
                composedStepsService.getComposedSteps()
        )
    }

    fun getStepTree(stepsTreeFilter: StepsTreeFilter): RootStepNode {
        val basicSteps = getBasicSteps(stepsTreeFilter)
        val composedSteps = getComposedSteps(stepsTreeFilter)

        val treeBuilder = StepTreeBuilder()

        basicSteps.forEach {
            treeBuilder.addBasicStepDef(it)
        }
        composedSteps.forEach {
            val composedStepWithWarnings = warningService.composedStepWithWarnings(it, keepExistingWarnings = true)

            treeBuilder.addComposedStepDef(composedStepWithWarnings)
        }

        return treeBuilder.build()
    }

    fun getDirectoriesTree(): ComposedContainerStepNode = composedStepsService.getDirectoriesTree()

    fun getWarnings(composedStepDef: ComposedStepDef, keepExistingWarnings: Boolean): ComposedStepDef
            = composedStepsService.getWarnings(composedStepDef, keepExistingWarnings)

}
