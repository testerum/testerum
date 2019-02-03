package com.testerum.web_backend.services.project

import com.testerum.file_service.caches.RecentProjectsCache
import com.testerum.model.home.Project
import com.testerum.web_backend.controllers.project.model.ProjectRequest
import java.nio.file.Paths
import java.time.LocalDateTime

class ProjectFrontendService(private val recentProjectsCache: RecentProjectsCache) {

    fun getProjects(): List<Project> {
        return recentProjectsCache.getAllProjects()
                .sortedByDescending { it.lastOpened }
    }

    fun createProject(createProjectRequest: ProjectRequest): Project {
        return recentProjectsCache.createProject(
                projectParentDir = Paths.get(createProjectRequest.projectParentDir),
                projectName = createProjectRequest.projectName
        )
    }

    fun openProject(path: String): Project {
        return recentProjectsCache.openProject(path)
    }

    fun deleteRecentProject(path: String) {
        recentProjectsCache.deleteRecentProject(
                Paths.get(path)
        )
    }

    fun renameProject(renameProjectRequest: ProjectRequest): Project {
//        TODO CRISTI: pls implement
        return Project(renameProjectRequest.projectName, renameProjectRequest.projectParentDir, LocalDateTime.now())
    }

}
