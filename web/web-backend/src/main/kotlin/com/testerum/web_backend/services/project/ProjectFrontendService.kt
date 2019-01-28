package com.testerum.web_backend.services.project

import com.testerum.file_service.caches.RecentProjectsCache
import com.testerum.model.home.Project
import com.testerum.web_backend.controllers.project.model.CreateProjectRequest
import java.nio.file.Paths

class ProjectFrontendService(private val recentProjectsCache: RecentProjectsCache) {

    fun getProjects(): List<Project> {
        return recentProjectsCache.getAllProjects()
                .sortedByDescending { it.lastOpened }
    }

    fun createProject(createProjectRequest: CreateProjectRequest): Project {
        return recentProjectsCache.createProject(
                projectParentDir = Paths.get(createProjectRequest.projectParentDir),
                projectName = createProjectRequest.projectName
        )
    }

}
