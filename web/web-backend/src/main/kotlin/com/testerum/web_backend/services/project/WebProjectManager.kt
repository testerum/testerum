package com.testerum.web_backend.services.project

import com.testerum.project_manager.ProjectManager
import com.testerum.project_manager.ProjectServices
import com.testerum.web_backend.filter.project.ProjectDirHolder

class WebProjectManager(private val projectManager: ProjectManager) {

    fun getProjectServices(): ProjectServices {
        val projectRootDir = ProjectDirHolder.get()

        return projectManager.getProjectServices(projectRootDir)
    }

    fun reloadProject() {
        val projectRootDir = ProjectDirHolder.get()

        projectManager.reloadProject(projectRootDir)
    }

}
