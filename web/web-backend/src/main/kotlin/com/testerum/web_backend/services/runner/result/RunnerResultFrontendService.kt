package com.testerum.web_backend.services.runner.result

import com.testerum.file_service.file.RunnerResultFileService
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Path as JavaPath

class RunnerResultFrontendService(private val frontendDirs: FrontendDirs,
                                  private val runnerResultFileService: RunnerResultFileService) {

    fun getResults(): List<RunnerResultsDirInfo> {
        val resultsDir = frontendDirs.getResultsDir()
                ?: return emptyList()

        return runnerResultFileService.getResults(resultsDir)
    }

    fun getResultAtPath(path: Path): List<RunnerEvent> {
        val resultsDir = frontendDirs.getResultsDir()
                ?: return emptyList()

        return runnerResultFileService.getResultAtPath(path, resultsDir)
    }

    fun createResultsFileName(): JavaPath {
        val resultsDir = frontendDirs.getResultsDir()
                ?: throw IllegalStateException("cannot create test result file because the resultsDir is not set")

        return runnerResultFileService.createResultsFileName(resultsDir)
    }

}
