package com.testerum.web_backend.services.runner.result

import com.testerum.file_service.file.RunnerResultFileService
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.web_backend.services.dirs.FrontendDirs

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

    fun saveEvent(runnerEvent: RunnerEvent,
                  file: Path) {
        val resultsDir = frontendDirs.getResultsDir()
                ?: throw IllegalStateException("cannot save runner event because the resultsDir is not set")

        runnerResultFileService.saveEvent(runnerEvent, file, resultsDir)
    }

    fun createResultsFileName(): Path = runnerResultFileService.createResultsFileName()

}
