package com.testerum.report_server.service

import com.testerum.common_kotlin.isDirectory
import com.testerum.report_server.config.ReportServerConfig
import com.testerum.report_server.config.ReportServerConfig.ENVIRONMENT_PROJECT_ID_SEPARATOR
import com.testerum.report_server.model.ReportInfo
import org.springframework.stereotype.Service
import java.nio.file.Files
import kotlin.streams.toList
import java.nio.file.Path as JavaPath


@Service
class GetReportsInfoService {

    fun getReportsInfo(): List<ReportInfo> {
        val reportsRootDirectory = ReportServerConfig.getReportsRootDirectory()
        val projectDirs = getSubDirectories(reportsRootDirectory)

        val result = mutableListOf<ReportInfo>()
        for (projectDir in projectDirs) {
            val projectReportsInfo: List<ReportInfo> = getReportsInfoForProject(projectDir)
            result.addAll(projectReportsInfo)
        }

        return result
    }

    private fun getReportsInfoForProject(projectDir: JavaPath): List<ReportInfo> {
        val result = mutableListOf<ReportInfo>()

        val environmentDirs = getSubDirectories(projectDir)

        for (envDir in environmentDirs) {
            val environmentWithProjectId = envDir.fileName.toString()

            if(!environmentWithProjectId.contains(ENVIRONMENT_PROJECT_ID_SEPARATOR)) continue //directory that was not generated by the Report Server. Might happen if you run the runner on the same computer

            val environmentName = environmentWithProjectId.substringBeforeLast(ENVIRONMENT_PROJECT_ID_SEPARATOR)
            val projectId = environmentWithProjectId.substringAfter(ENVIRONMENT_PROJECT_ID_SEPARATOR)

            val statisticsReportPath = envDir.resolve("statistics.html")
            val latestReportPath = envDir.resolve("latest-report.html")
            val autoRefreshDashboardPath = envDir.resolve("autorefresh-dashboard.html")

            val reportsRootDirectory = ReportServerConfig.getReportsRootDirectory()

            result.add(
                ReportInfo(
                    projectName = projectDir.fileName.toString(),
                    projectEnvironment = environmentName,
                    projectId = projectId,
                    statisticsReportPath = asUrlPath(reportsRootDirectory.relativize(statisticsReportPath)),
                    latestReportPath = asUrlPath(reportsRootDirectory.relativize(latestReportPath)),
                    autoRefreshDashboardPath = asUrlPath(reportsRootDirectory.relativize(autoRefreshDashboardPath))
                )
            )
        }

        return result
    }

    private fun asUrlPath(path: JavaPath): String {
        val url = StringBuilder()
        for (pathPart in path) {
            url.append("/").append(pathPart.toString())
            println("pathPart = ${pathPart}")

        }
        return url.toString()
    }

    private fun getSubDirectories(reportsRootDirectory: JavaPath): List<JavaPath> {
        Files.walk(reportsRootDirectory, 1).use { stream ->
            return stream
                .filter { it.isDirectory }
                .toList()
                .drop(1)
        }
    }
}
