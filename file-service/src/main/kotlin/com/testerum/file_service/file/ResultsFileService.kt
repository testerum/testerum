package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.list
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultFileInfo
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.cmdline.report_type.model.json_stats.JsonStatistics
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

class ResultsFileService {

    companion object {
        private val LOG = LoggerFactory.getLogger(ResultsFileService::class.java)

        private val DAY_DIR_NAME_REGEX = Regex("""[0-9]{4}-[0-9]{2}-[0-9]{2}""")

        private val OBJECT_MAPPER = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun getReports(reportsDir: JavaPath): List<RunnerResultsDirInfo> {
        val reportDayDirs: List<JavaPath> = reportsDir.list { path ->
            DAY_DIR_NAME_REGEX.matches(path.fileName.toString())
        }

        val reportDayDirInfos = reportDayDirs.map {
            createRunnerResultsDirInfo(reportsDir, it)
        }

        return reportDayDirInfos.sortedByDescending { it.directoryName }
    }

    private fun createRunnerResultsDirInfo(reportsDir: JavaPath,
                                           reportDayDir: JavaPath): RunnerResultsDirInfo {
        val executionDirs: List<JavaPath> = reportDayDir.list()

        val runnerResultFilesInfo = executionDirs.map { createRunnerResultFileInfo(reportsDir, it) }
                .filterNotNull()
                .sortedBy { it.name }

        return RunnerResultsDirInfo(
                directoryName = reportDayDir.fileName.toString(),
                runnerResultFilesInfo = runnerResultFilesInfo
        )
    }

    private fun createRunnerResultFileInfo(reportsDir: JavaPath,
                                           executionDir: JavaPath): RunnerResultFileInfo? {
        val relativePath: JavaPath = reportsDir.relativize(executionDir)

        val stats: JsonStatistics = loadStatistics(executionDir)
                ?: return null

        // The URL will be filled-in by the web module (since URL-s are a web concern).
        // I chose to do it this way to minimize the number of model classes -
        // normally the "file-service" module would need to return a model without "url".
        val url = "";

        return RunnerResultFileInfo(
                path = Path.createInstance(relativePath.toString()),
                name = executionDir.fileName.toString(),
                url = url,
                executionResult = stats.executionStatus,
                durationMillis = stats.durationMillis
        )
    }

    private fun loadStatistics(executionDir: JavaPath): JsonStatistics? {
        val statsFile = executionDir.resolve("json_stats").resolve("stats.json")
                .toAbsolutePath()
                .normalize()

        return try {
            OBJECT_MAPPER.readValue(statsFile.toFile())
        } catch (e: Exception) {
            LOG.warn("failed to parse [$statsFile]", e)
            null
        }
    }

}
