package com.testerum.web_backend.services.runner.config

import com.testerum.file_service.file.RunConfigFileService
import com.testerum.model.runner.config.RunConfig
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.project.WebProjectManager
import java.nio.file.Path as JavaPath

class RunConfigFrontendService(private val frontendDirs: FrontendDirs,
                               private val webProjectManager: WebProjectManager,
                               private val runConfigFileService: RunConfigFileService) {

    fun getRunConfigs(): List<RunConfig> {
        return runConfigFileService.getRunConfigs(getRunConfigsDir())
    }

    fun saveRunConfig(runConfigs: List<RunConfig>): List<RunConfig> {
        return runConfigFileService.save(runConfigs, getRunConfigsDir())
    }

    private fun getRunConfigsDir(): JavaPath {
        val runConfigsDir: JavaPath = frontendDirs.getRunConfigsDir()
        val projectId = webProjectManager.getProjectServices().project.id

        return runConfigsDir.resolve(projectId)
    }
}
