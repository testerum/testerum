package com.testerum.web_backend.services.runner.result

import com.testerum.file_service.file.ResultsFileService
import com.testerum.model.run_result.RunnerResultFileInfo
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Path as JavaPath

class ResultsFrontendService(private val frontendDirs: FrontendDirs,
                             private val resultsFileService: ResultsFileService) {

    fun getResults(): List<RunnerResultsDirInfo> {
        val reportsDir: JavaPath = frontendDirs.getReportsDir()

        val reports = resultsFileService.getReports(reportsDir)

        return setReportsUrlsForDirs(reports)
    }

    fun createResultsDirectoryName(): JavaPath {
        val reportsDir: JavaPath = frontendDirs.getReportsDir()

        return resultsFileService.createResultsDirectoryName(reportsDir)
    }

    private fun setReportsUrlsForDirs(reports: List<RunnerResultsDirInfo>): List<RunnerResultsDirInfo> {
        return reports.map {
            setReportsUrlsForDir(it)
        }
    }

    private fun setReportsUrlsForDir(dir: RunnerResultsDirInfo): RunnerResultsDirInfo {
        return dir.copy(
                runnerResultFilesInfo = dir.runnerResultFilesInfo.map {
                    setReportsUrlsForReport(it)
                }
        )

    }

    private fun setReportsUrlsForReport(report: RunnerResultFileInfo): RunnerResultFileInfo {
        return report.copy(
                url = "/rest/report-results/files/${report.path}/pretty/index.html"
        )
    }

}
