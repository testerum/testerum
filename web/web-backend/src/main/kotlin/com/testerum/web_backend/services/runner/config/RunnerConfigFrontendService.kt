package com.testerum.web_backend.services.runner.config

import com.testerum.file_service.file.RunConfigFileService
import com.testerum.model.runner.config.RunnerConfig
import com.testerum.web_backend.services.project.WebProjectManager

class RunnerConfigFrontendService(private val webProjectManager: WebProjectManager,
                                  private val runConfigFileService: RunConfigFileService) {

    fun getRunConfigs(): List<RunnerConfig> {
        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        return runConfigFileService.getAllRunConfigs(projectRootDir)
    }

    fun saveRunnerConfig(runnerConfigs: List<RunnerConfig>): List<RunnerConfig> {
        val savedRunConfigs = mutableListOf<RunnerConfig>()

        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        for (runnerConfig in runnerConfigs) {
            val savedRunConfig = runConfigFileService.save(runnerConfig, projectRootDir)

            savedRunConfigs += savedRunConfig
        }

        return savedRunConfigs
    }

}
