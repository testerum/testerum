package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory
import java.nio.file.Path

class StepsCacheInitializer(private val frontendDirs: FrontendDirs,
                            private val stepsCache: StepsCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(StepsCacheInitializer::class.java)
    }

    fun initialize() {
        val basicStepsDir = frontendDirs.getBasicStepsDir()

        val cacheDir = frontendDirs.getCacheDir()
        val resourcesDir = frontendDirs.getOptionalResourcesDir()
        val composedStepsDir = frontendDirs.getOptionalComposedStepsDir()

        stepsCache.initialize(
                stepLibraryJarFiles = getStepLibraryJarFiles(basicStepsDir),
                persistentCacheFile = cacheDir.resolve("basic-steps-cache.json"),
                composedStepsDir = composedStepsDir,
                resourcesDir = resourcesDir
        )
    }

    private fun getStepLibraryJarFiles(basicStepsDir: Path): List<Path> {
        return basicStepsDir.walkAndCollect {
            it.isRegularFile && it.hasExtension(".jar")
        }
    }

    fun reinitializeComposedSteps() {
        stepsCache.reinitializeComposedSteps()
    }

}
