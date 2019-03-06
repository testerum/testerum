package com.testerum.web_backend.services.runner.result

import com.testerum.common_kotlin.exists
import com.testerum.file_service.file.ResultsFileService
import com.testerum.model.run_result.RunnerResultFileInfo
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.project.WebProjectManager
import java.nio.file.Path as JavaPath

class ResultsFrontendService(private val frontendDirs: FrontendDirs,
                             private val resultsFileService: ResultsFileService,
                             private val webProjectManager: WebProjectManager) {

    fun getResults(): List<RunnerResultsDirInfo> {
        val reportsDir: JavaPath = frontendDirs.getReportsDir(
                projectId = webProjectManager.getProjectServices().project.id
        )

        val reports = resultsFileService.getReports(reportsDir)
        val reportsWithUrlsForDirs = setReportsUrlsForDirs(reports)

        return reportsWithUrlsForDirs
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
        val projectId = webProjectManager.getProjectServices().project.id

        return report.copy(
                url = "/rest/report-results/files/$projectId/${report.path}/pretty/index.html"
        )
    }

    fun getStatisticsUrl(): String? {
        val projectId = webProjectManager.getProjectServices().project.id
        val statisticsIndexFile = frontendDirs.getAggregatedStatisticsDir(projectId)
                .resolve("index.html")

        if (statisticsIndexFile.exists) {
            return "/rest/report-results/files/$projectId/statistics/index.html"
        } else {
            return null
        }
    }

}
