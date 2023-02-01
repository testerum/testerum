package com.testerum.runner_cmdline.project_manager

import com.testerum.project_manager.ProjectManager
import com.testerum.project_manager.ProjectServices
import java.nio.file.Path as JavaPath

class RunnerProjectManager(private val projectManager: ProjectManager,
                           private val projectRootDir: JavaPath) {

    fun getProjectServices(): ProjectServices = projectManager.getProjectServices(projectRootDir)

}
