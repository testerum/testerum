package com.testerum.file_service.caches.resolved

import com.testerum.common_kotlin.walk
import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.ComposedStepFileService
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.util.isChangedRequiringSave
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.step.tree.ComposedContainerStepNode
import com.testerum.model.step.tree.builder.ComposedStepDirectoryTreeBuilder
import com.testerum.model.text.StepPattern
import com.testerum.model.util.StepHashUtil
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class StepsCache(private val basicStepsCache: BasicStepsCache,
                 private val composedStepFileService: ComposedStepFileService,
                 private val stepsResolver: StepsResolver,
                 private val warningService: WarningService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(StepsCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var composedStepsDir: JavaPath? = null
    private var resourcesDir: JavaPath? = null

    private var unresolvedComposedSteps: MutableList<ComposedStepDef> = mutableListOf()

    private var stepsByHash: MutableMap</*hash: */String, StepDef> = hashMapOf()
    private var stepsByPath: MutableMap<Path, StepDef> = hashMapOf()

    fun initialize(composedStepsDir: JavaPath?,
                   resourcesDir: JavaPath?) {
        lock.write {
            if (composedStepsDir == null || resourcesDir == null) {
                LOG.info("not loading composed steps because either the composed steps dir or the resources dir is unknown")
            } else {
                this.composedStepsDir = composedStepsDir
                this.resourcesDir = resourcesDir

                // load unresolved composed steps
                this.unresolvedComposedSteps = ArrayList(loadComposedSteps(composedStepsDir))

                resolveSteps()
            }
        }
    }

    fun reinitializeComposedSteps() {
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
                    ?: throw IllegalStateException("cannot delete composed step because the composedStepsDir setting is not set")

            composedStepFileService.deleteComposedStep(path, composedStepsDir)

            unresolvedComposedSteps.removeIf { it.oldPath == path }

            resolveSteps()
        }
    }

    fun saveComposedStep(composedStep: ComposedStepDef): ComposedStepDef {
        lock.write {
            val composedStepsDir = this.composedStepsDir
                    ?: throw IllegalStateException("cannot save composed step because the composedStepsDir setting is not set")

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
                    ?: throw IllegalStateException("cannot rename directory step because the composedStepsDir setting is not set")

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
                    ?: throw IllegalStateException("cannot delete directory step because the composedStepsDir setting is not set")

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
                    ?: throw IllegalStateException("cannot move composed step directory or file because the composedStepsDir setting is not set")

            composedStepFileService.moveComposedStepDirectoryOrFile(copyPath, composedStepsDir)

            this.unresolvedComposedSteps = ArrayList(loadComposedSteps(composedStepsDir))

            resolveSteps()
        }
    }

    fun getComposedStepsDirectoriesTree(): ComposedContainerStepNode {
        val composedStepsDir = this.composedStepsDir
                ?: throw IllegalStateException("cannot get composed steps directories tree because the composedStepsDir setting is not set")

        val treeBuilder = ComposedStepDirectoryTreeBuilder()

        composedStepsDir.walk { path ->
            if (Files.isDirectory(path) && (path != composedStepsDir)) {
                val relativeDirectory = composedStepsDir.relativize(path)
                val relativeDirectoryPathParts: List<String> = relativeDirectory.map { it.fileName.toString() }

                treeBuilder.addComposedStepDirectory(relativeDirectoryPathParts)
            }
        }

        return treeBuilder.build()

    }

    private val StepDef.hash: String
        get() = StepHashUtil.calculateStepHash(this)

}
