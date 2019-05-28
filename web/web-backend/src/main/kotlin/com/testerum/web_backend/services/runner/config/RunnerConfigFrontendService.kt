package com.testerum.web_backend.services.runner.config

import com.testerum.file_service.file.RunConfigFileService
import com.testerum.model.runner.config.RunnerConfig
import com.testerum.web_backend.services.project.WebProjectManager
import org.springframework.web.bind.annotation.RequestBody

class RunnerConfigFrontendService(private val webProjectManager: WebProjectManager,
                                  private val runConfigFileService: RunConfigFileService) {

    fun getRunnerConfigs(): List<RunnerConfig> {
        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        return runConfigFileService.getAllRunConfigs(projectRootDir)
    }

    fun saveRunnerConfig(@RequestBody runnerConfig: RunnerConfig): RunnerConfig {
        val projectRootDir = webProjectManager.getProjectServices().dirs().projectRootDir

        return runConfigFileService.save(runnerConfig, projectRootDir)
    }

}
