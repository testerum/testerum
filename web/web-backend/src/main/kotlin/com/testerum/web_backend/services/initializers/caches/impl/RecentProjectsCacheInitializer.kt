package com.testerum.web_backend.services.initializers.caches.impl

import com.testerum.file_service.caches.RecentProjectsCache
import com.testerum.web_backend.services.dirs.FrontendDirs

class RecentProjectsCacheInitializer(private val frontendDirs: FrontendDirs,
                                     private val recentProjectsCache: RecentProjectsCache) {

    fun initialize() {
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        recentProjectsCache.initialize(recentProjectsFile)
    }

}
