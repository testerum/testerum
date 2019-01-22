package com.testerum.web_backend.services.project

import com.testerum.file_service.caches.RecentProjectsCache
import com.testerum.model.home.Project

class ProjectFrontendService(private val recentProjectsCache: RecentProjectsCache) {

    fun getProjects(): List<Project> {
        return recentProjectsCache.getAllProjects()
                .sortedByDescending { it.lastOpened }
    }

}
