package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.web_backend.services.dirs.FrontendDirs

class BasicStepsCacheInitializer(private val frontendDirs: FrontendDirs,
                                 private val basicStepsCache: BasicStepsCache) {

    fun initialize() {
        basicStepsCache.initialize()
    }

}
