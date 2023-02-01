package com.testerum.report_generators.dirs

import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.isNotADirectory
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.streams.toList
import java.nio.file.Path as JavaPath

object ReportDirs {

    private val DAY_DIR_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val DAY_DIR_RECOGNIZER = Regex("""\d{4}-\d{2}-\d{2}""")

    private val EXECUTION_DIR_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss-SSS")

    private fun getInstallDir(): JavaPath = run {
        val packageDirectoryProperty = System.getProperty("testerum.packageDirectory")
            ?: throw IllegalArgumentException("missing required [testerum.packageDirectory] system property")

        val path: JavaPath = Paths.get(packageDirectoryProperty)
        val absolutePath: JavaPath = path.toAbsolutePath().normalize()

        if (absolutePath.doesNotExist) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) does not exist")
        }
        if (absolutePath.isNotADirectory) {
            throw IllegalArgumentException("package directory [$path] (resolved as absolutePath) is not a directory")
        }

        return@run absolutePath
    }

    private fun getRunnerDir(): JavaPath = getInstallDir().resolve("runner")

    fun getReportTemplatesDir(): JavaPath = getRunnerDir().resolve("report_templates")

    fun getLatestReportSymlink(managedReportsDir: JavaPath): JavaPath = managedReportsDir.resolve("latest")
    fun getReportsPrettyDir(executionDir: JavaPath): JavaPath = executionDir.resolve("pretty")
    fun getReportsStatsFileName(executionDir: JavaPath): JavaPath = executionDir.resolve("json_stats")
        .resolve("stats.json") // todo: remove duplication between this method and ResultsFileService.loadStatistics()

    fun getFullStatsFileName(executionDir: JavaPath): JavaPath =
        executionDir.resolve("full_stats").resolve("stats.json")

    fun getAggregatedStatisticsDir(managedReportsDir: JavaPath): JavaPath = managedReportsDir.resolve("statistics")
    fun getAggregatedStatisticsJsonFile(managedReportsDir: JavaPath): JavaPath =
        getAggregatedStatisticsDir(managedReportsDir).resolve("stats.json")

    fun createResultsDirectoryName(managedReportsDir: JavaPath): JavaPath {
        val localDate: LocalDateTime = LocalDateTime.now()
        val dayDirName: String = localDate.format(DAY_DIR_NAME_FORMATTER)
        val executionDirName: String = localDate.format(EXECUTION_DIR_NAME_FORMATTER)

        return managedReportsDir
            .resolve(dayDirName)
            .resolve(executionDirName)
    }

    fun processExecutionDirs(
        managedReportsDir: JavaPath,
        process: (executionDir: JavaPath) -> Unit
    ) {
        if (managedReportsDir.doesNotExist) {
            return
        }

        for (dayDir in managedReportsDir.listingSortedByLastModified()) {
            processExecutionDirsForDayDir(dayDir, process)
        }
    }

    private fun processExecutionDirsForDayDir(
        dayDir: JavaPath,
        process: (executionDir: JavaPath) -> Unit
    ) {
        if (dayDir.doesNotExist) {
            return
        }
        if (!isDayDir(dayDir)) {
            return
        }

        for (executionDir in dayDir.listingSortedByLastModified()) {
            process(executionDir)
        }
    }


    private fun isDayDir(dir: JavaPath): Boolean {
        val fileName = dir.fileName
            ?: return false

        return DAY_DIR_RECOGNIZER.matches(fileName.toString())
    }

    private fun JavaPath.listingSortedByLastModified(): List<JavaPath> {
        return Files.list(this).use { pathStream ->
            pathStream.toList()
                .sortedBy { Files.getLastModifiedTime(it) }
        }
    }

}
