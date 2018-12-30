package com.testerum.web_backend.services.runner.result

import com.testerum.file_service.file.RunnerResultFileService
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Path as JavaPath

class RunnerResultFrontendService(private val frontendDirs: FrontendDirs,
                                  private val runnerResultFileService: RunnerResultFileService) {

    fun getResults(): List<RunnerResultsDirInfo> {
        val reportsDir: JavaPath = frontendDirs.getReportsDir()

        return runnerResultFileService.getReports(reportsDir)
    }

    fun createResultsDirectoryName(): JavaPath {
        val reportsDir: JavaPath = frontendDirs.getReportsDir()

        return runnerResultFileService.createResultsDirectoryName(reportsDir)
    }

}
