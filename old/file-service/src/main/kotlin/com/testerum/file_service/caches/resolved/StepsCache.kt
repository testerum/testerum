package com.testerum.file_service.caches.resolved

import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.ComposedStepFileService
import com.testerum.file_service.util.isChangedRequiringSave
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.util.StepHashUtil
import com.testerum.model.util.escape
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class StepsCache(
    private val basicStepsCache: BasicStepsCache,
    private val composedStepFileService: ComposedStepFileService,
    private val stepsResolver: StepsResolver,
    private val warningService: WarningService
) {

    companion object {
        private val LOG = LoggerFactory.getLogger(StepsCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var composedStepsDir: JavaPath? = null
    private var resourcesDir: JavaPath? = null

    private var unresolvedComposedSteps: MutableList<ComposedStepDef> = mutableListOf()

    private var stepsByHash: MutableMap</*hash: */String, StepDef> = hashMapOf()
    private var stepsByPath: MutableMap<Path, StepDef> = hashMapOf()

    fun initialize(
        packagesWithAnnotations: List<String>,
        additionalBasicStepsDirs: List<JavaPath>,
        composedStepsDir: JavaPath,
        resourcesDir: JavaPath,
    ) {
        lock.write {
            this.composedStepsDir = composedStepsDir
            this.resourcesDir = resourcesDir

            basicStepsCache.initialize(packagesWithAnnotations, additionalBasicStepsDirs)

            // load unresolved composed steps
            this.unresolvedComposedSteps = ArrayList(loadComposedSteps(composedStepsDir))

            resolveSteps()
        }
    }

    private fun reinitializeComposedSteps() {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot resolve steps because the composedStepsDir is not set")

            // load unresolved composed steps
            this.unresolvedComposedSteps = ArrayList(loadComposedSteps(composedStepsDir))

            resolveSteps()
        }
    }

    private fun loadComposedSteps(composedStepsDir: JavaPath): List<ComposedStepDef> {
        val startTimeMillis = System.currentTimeMillis()

        val composedSteps: List<ComposedStepDef> = composedStepFileService.getAllComposedSteps(composedStepsDir)

        val endTimeInitMillis = System.currentTimeMillis()
        LOG.info("loading ${composedSteps.size} composed steps took ${endTimeInitMillis - startTimeMillis} ms")

        return composedSteps
    }

    private fun resolveSteps() {
        val resourcesDir = this.resourcesDir
            ?: throw IllegalStateException("cannot resolve steps because the resourcesDir is not set")

        // resolve steps
        val basicSteps = basicStepsCache.getBasicSteps()
        stepsByHash = resolveSteps(basicSteps, this.unresolvedComposedSteps, resourcesDir)

        stepsByPath = hashMapOf()
        stepsByHash.mapKeysTo(stepsByPath) { it.value.path }
    }

    private fun resolveSteps(basicSteps: Collection<BasicStepDef>, composedSteps: List<ComposedStepDef>, resourcesDir: JavaPath): MutableMap<String, StepDef> {
        val startTimeMillis = System.currentTimeMillis()

        val basicStepsByHash = basicSteps.associateBy { it.hash }
        val unresolvedComposedStepsByHash = composedSteps.associateBy { it.hash }

        val resolvedSteps = stepsResolver.resolve(basicStepsByHash, unresolvedComposedStepsByHash, resourcesDir)
        val resolvedStepsWithWarnings = addWarnings(resolvedSteps)

        val endTimeInitMillis = System.currentTimeMillis()
        LOG.info("resolving ${resolvedStepsWithWarnings.size} steps took ${endTimeInitMillis - startTimeMillis} ms")

        return resolvedStepsWithWarnings
    }

    private fun addWarnings(resolvedSteps: Map</*hash: */String, StepDef>): MutableMap</*hash: */String, StepDef> {
        val result = hashMapOf<String, StepDef>()

        for ((hash, step) in resolvedSteps) {
            if (step is ComposedStepDef) {
                val stepWithWarnings = warningService.composedStepWithWarnings(step)

                result[stepWithWarnings.hash] = stepWithWarnings
            } else {
                result[hash] = step
            }
        }

        return result
    }

    fun getBasicSteps(): List<BasicStepDef> = lock.read { basicStepsCache.getBasicSteps() }

    fun getBasicStepAtPath(basicStepPath: Path): BasicStepDef? = lock.read { basicStepsCache.getStepAtPath(basicStepPath) }

    fun getHooks(): List<HookDef> = lock.read { basicStepsCache.getHooks() }

    fun getAllSteps(): Collection<StepDef> = lock.read { stepsByHash.values }

    fun getStepAtPath(basicStepPath: Path): StepDef? = lock.read { stepsByPath[basicStepPath] }

    fun getComposedStepAtPath(path: Path): ComposedStepDef? {
        val step = getStepAtPath(path)
            ?: return null

        if (step !is ComposedStepDef) {
            return null
        }

        return step
    }

    fun getStepDefByPhaseAndPattern(stepPhase: StepPhaseEnum, stepPattern: StepPattern): StepDef {
        lock.read {
            val stepHash = StepHashUtil.calculateStepHash(stepPhase, stepPattern)

            return stepsByHash[stepHash]
                ?: UndefinedStepDef(stepPhase, stepPattern)
        }
    }

    fun getComposedSteps(): List<ComposedStepDef> = getAllSteps().filterIsInstance<ComposedStepDef>()

    fun deleteComposedStep(path: Path) {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot delete composed step because the composedStepsDir is not set")

            composedStepFileService.deleteComposedStep(path, composedStepsDir)

            unresolvedComposedSteps.removeIf { it.oldPath == path }

            resolveSteps()
        }
    }

    fun saveComposedStep(composedStep: ComposedStepDef): ComposedStepDef {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot save composed step because the composedStepsDir is not set")

            val existingStep = composedStep.oldPath?.let { stepsByPath[it] } as? ComposedStepDef

            // update without changes
            if (existingStep != null && !composedStep.isChangedRequiringSave(existingStep)) {
                return existingStep
            }

            // save file
            val savedComposedStepDef = composedStepFileService.save(composedStep, composedStepsDir)

            // update cache: remove old
            val oldPath = composedStep.oldPath
            val newPath = composedStep.getNewPath()
            if (oldPath != null) {
                unresolvedComposedSteps.removeIf { it.path == oldPath }
            }
            unresolvedComposedSteps.removeIf { it.path == newPath }

            // update cache: add new
            unresolvedComposedSteps.add(savedComposedStepDef)

            // re-resolve steps
            resolveSteps()

            return getStepAtPath(savedComposedStepDef.path)!! as ComposedStepDef
        }
    }

    fun renameComposedStepDirectory(renamePath: RenamePath): Path {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot rename directory step because the composedStepsDir is not set")

            // rename directory
            val oldEscapedPath = renamePath.path.escape()
            val newEscapedPath = composedStepFileService.renameDirectory(renamePath, composedStepsDir)
                .escape()

            // update cache
            val newUnresolvedComposedSteps: MutableList<ComposedStepDef> = mutableListOf()

            for (step in unresolvedComposedSteps) {
                val newStepPath = step.path.replaceDirs(oldEscapedPath, newEscapedPath)

                newUnresolvedComposedSteps += step.copy(
                    path = newStepPath,
                    oldPath = newStepPath
                )
            }

            unresolvedComposedSteps = newUnresolvedComposedSteps
            resolveSteps()

            return newEscapedPath
        }
    }

    fun deleteComposedStepDirectory(path: Path) {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot delete directory step because the composedStepsDir is not set")

            // delete directory
            composedStepFileService.deleteDirectory(path, composedStepsDir)

            // update cache
            val newUnresolvedComposedSteps: MutableList<ComposedStepDef> = mutableListOf()
            unresolvedComposedSteps.filterTo(newUnresolvedComposedSteps) {
                !it.path.isChildOrSelf(path)
            }
            unresolvedComposedSteps = newUnresolvedComposedSteps
            resolveSteps()
        }
    }

    fun moveComposedStepDirectoryOrFile(copyPath: CopyPath) {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot move composed step directory or file because the composedStepsDir is not set")

            composedStepFileService.moveComposedStepDirectoryOrFile(copyPath, composedStepsDir)

            reinitializeComposedSteps()
        }
    }

    fun copyComposedStep(sourcePath: Path, destinationDirPath: Path): Path {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot move composed step directory or file because the composedStepsDir is not set")

            // lookup step def
            val stepDef = getComposedStepAtPathOrFailWithError(sourcePath)

            // change pattern
            var stepDefToSave = createComposedStepDefCopyWithUniquePattern(stepDef, destinationDirPath)
            stepDefToSave = stepDefToSave.copy(
                oldPath = null, // setting old-path to "null" to tell "save" to create
                path = stepDefToSave.getNewPath()
            )

            // save
            val savedComposedStepDef = composedStepFileService.save(stepDefToSave, composedStepsDir)

            // reload steps
            reinitializeComposedSteps()

            return savedComposedStepDef.path
        }
    }

    fun moveComposedStep(sourcePath: Path, destinationDirPath: Path): Path {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot move composed step directory or file because the composedStepsDir is not set")

            // lookup step def
            val stepDefs = getComposedStepsAtPathOrFailWithError(sourcePath)

            val oldPath = sourcePath.getParent()
            for (stepDef in stepDefs) {

                val stepDefNewPath = stepDef.path.replaceDirs(oldPath, destinationDirPath)

                val stepDefToSave = stepDef.copy(path = stepDefNewPath)

                // save
                composedStepFileService.save(stepDefToSave, composedStepsDir)

                // reload steps
                reinitializeComposedSteps()
            }

            return sourcePath.replaceDirs(oldPath, destinationDirPath)
        }
    }

    private fun getComposedStepsAtPathOrFailWithError(sourcePath: Path): Collection<ComposedStepDef> {
        lock.read {
            if (sourcePath.isFile()) {
                val composedStepDef = getComposedStepAtPathOrFailWithError(sourcePath)
                return listOf(composedStepDef)
            }

            val stepsToMove = stepsByPath
                .filter { it.key.isChildOrSelf(sourcePath) }
                .filter { it.value is ComposedStepDef}
                .mapValues { it.value as ComposedStepDef }
                .values.toList()

            if (stepsToMove.isEmpty()) {
                throw ValidationException(
                    globalMessage = "The path [$sourcePath] does not contain any steps.",
                    globalHtmlMessage = "The step at path<br/><code>$sourcePath</code><br/>does not exist."
                )
            }

            return stepsToMove
        }
    }

    private fun getComposedStepAtPathOrFailWithError(sourcePath: Path): ComposedStepDef {
        lock.read {
            val stepDef = stepsByPath[sourcePath]
            if (stepDef == null) {
                throw ValidationException(
                    globalMessage = "The step at path [$sourcePath] does not exist.",
                    globalHtmlMessage = "The step at path<br/><code>$sourcePath</code><br/>does not exist."
                )
            }
            if (stepDef !is ComposedStepDef) {
                throw ValidationException(
                    globalMessage = "The step at path [$sourcePath] is not a composed step.",
                    globalHtmlMessage = "The step at path<br/><code>$sourcePath</code><br/>is not a composed step."
                )
            }

            return stepDef
        }
    }

    private fun createComposedStepDefCopyWithUniquePattern(
        sourceStepDef: ComposedStepDef,
        destinationDirPath: Path
    ): ComposedStepDef {
        lock.read {
            var destinationPattern: StepPattern

            val postfixText = " - Copy"

            var postfix: String
            var postfixCount = 1

            do {
                val postfixCountAsString = if (postfixCount == 1) {
                    ""
                } else {
                    " $postfixCount"
                }

                postfix = postfixText + postfixCountAsString
                postfixCount++

                destinationPattern = sourceStepDef.stepPattern.appendPart(
                    TextStepPatternPart(postfix)
                )
            } while (stepExists(sourceStepDef.phase, destinationPattern))

            return sourceStepDef.copy(
                stepPattern = destinationPattern,
                oldPath = null,
                path = destinationDirPath.copy(
                    fileName = sourceStepDef.path.fileName,
                    fileExtension = sourceStepDef.path.fileExtension
                )
            )
        }
    }

    private fun stepExists(stepPhase: StepPhaseEnum, stepPattern: StepPattern): Boolean {
        lock.read {
            val stepHash = StepHashUtil.calculateStepHash(stepPhase, stepPattern)

            return stepsByHash[stepHash] != null
        }
    }

    private val StepDef.hash: String
        get() = StepHashUtil.calculateStepHash(this)

}
