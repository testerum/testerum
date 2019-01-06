package com.testerum.runner_cmdline.dirs

import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.isNotADirectory
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Path as JavaPath

object RunnerDirs {

    private val DAY_DIR_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val EXECUTION_DIR_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss-SSS")

    fun getPackageDir(): JavaPath = run {
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

    fun getDefaultBasicStepsDir(): JavaPath = getPackageDir().resolve("basic_steps")

    fun getRunnerDir(): JavaPath = getPackageDir().resolve("runner")

    fun getRunnerNodeDir(): JavaPath = getRunnerDir().resolve("node")

    fun getReportTemplatesDir(): JavaPath = getRunnerDir().resolve("report_templates")

    fun getLatestReportSymlink(managedReportsDir: JavaPath): JavaPath = managedReportsDir.resolve("latest")
    fun getReportsPrettyDir(executionDir: JavaPath): JavaPath = executionDir.resolve("pretty")
    fun getReportsStatsFileName(executionDir: JavaPath): JavaPath = executionDir.resolve("json_stats").resolve("stats.json") // todo: remove duplication between this method and ResultsFileService.loadStatistics()

    fun createResultsDirectoryName(managedReportsDir: JavaPath): JavaPath {
        val localDate: LocalDateTime = LocalDateTime.now()
        val dayDirName: String = localDate.format(DAY_DIR_NAME_FORMATTER)
        val executionDirName: String = localDate.format(EXECUTION_DIR_NAME_FORMATTER)

        return managedReportsDir
                .resolve(dayDirName)
                .resolve(executionDirName)
    }

}
