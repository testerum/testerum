package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Path

class BasicStepsCacheInitializer(private val frontendDirs: FrontendDirs,
                                 private val basicStepsCache: BasicStepsCache) {

    fun initialize() {
        val basicStepsDir = frontendDirs.getBasicStepsDir()

        val cacheDir = frontendDirs.getCacheDir()

        basicStepsCache.initialize(
                stepLibraryJarFiles = getStepLibraryJarFiles(basicStepsDir),
                persistentCacheFile = cacheDir.resolve("basic-steps-cache.json")
        )
    }

    private fun getStepLibraryJarFiles(basicStepsDir: Path): List<Path> {
        return basicStepsDir.walkAndCollect {
            it.isRegularFile && it.hasExtension(".jar")
        }
    }

}
