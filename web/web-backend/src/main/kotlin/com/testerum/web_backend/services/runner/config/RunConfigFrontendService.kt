package com.testerum.web_backend.services.runner.config

import com.testerum.file_service.file.RunConfigFileService
import com.testerum.model.runner.config.RunConfig
import com.testerum.web_backend.services.project.WebProjectManager

class RunConfigFrontendService(private val webProjectManager: WebProjectManager,
                               private val runConfigFileService: RunConfigFileService) {

    fun getRunConfigs(): List<RunConfig> {
        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        return runConfigFileService.getAllRunConfigs(projectRootDir)
    }

    fun saveRunConfig(runConfigs: List<RunConfig>): List<RunConfig> {
        val savedRunConfigs = mutableListOf<RunConfig>()

        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        for (runConfig in runConfigs) {
            val savedRunConfig = runConfigFileService.save(runConfig, projectRootDir)

            savedRunConfigs += savedRunConfig
        }

        return savedRunConfigs
    }

}
