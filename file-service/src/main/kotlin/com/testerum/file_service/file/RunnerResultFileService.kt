package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_kotlin.*
import com.testerum.file_service.file.util.escape
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.run_result.RunnerResultFileInfo
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.file.Path as JavaPath

class RunnerResultFileService {

    companion object {
        private val FILE_NAME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss_SSS")
        private const val FILE_EXTENSION = "result"

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

    fun getResults(resultsDir: JavaPath): List<RunnerResultsDirInfo> {
        val directoryInfos = mutableListOf<RunnerResultsDirInfo>()

        val resultFiles = resultsDir.walkAndCollect {
            it.isRegularFile && it.hasExtension(".result")
        }

        val resultsGroupedByDirectory: Map<String, List<JavaPath>> = resultFiles.groupBy { resultFile ->
            val relativeResultPath = resultsDir.relativize(resultFile)

            relativeResultPath.parent?.toString().orEmpty()
        }

        for ((directoryName, files) in resultsGroupedByDirectory) {
            val fileInfos = files.map { createRunnerResultFileInfo(it, resultsDir) }
                                                        .sortedBy { it.name }

            directoryInfos.add(
                    RunnerResultsDirInfo(
                            directoryName = directoryName,
                            runnerResultFilesInfo = fileInfos
                    )
            )
        }

        directoryInfos.sortBy { it.directoryName }

        return directoryInfos
    }

    private fun createRunnerResultFileInfo(resultFile: JavaPath,
                                           resultsDir: JavaPath): RunnerResultFileInfo {
        val runnerEvents: List<RunnerEvent> = parseResultFile(resultFile)

        var status: ExecutionStatus? = null
        var durationMillis: Long? = null

        val suiteEndEvent = runnerEvents.find { it is SuiteEndEvent }
                as? SuiteEndEvent
        if (suiteEndEvent != null) {
            status = suiteEndEvent.status
            durationMillis = suiteEndEvent.durationMillis
        }

        val relativeResultPath = resultsDir.relativize(resultFile)

        return RunnerResultFileInfo(
                path = Path.createInstance(
                        relativeResultPath.toString()
                ),
                name = resultFile.fileName?.toString().orEmpty(),
                executionResult = status,
                durationMillis = durationMillis
        )
    }


    fun getResultAtPath(path: Path,
                        resultsDir: JavaPath): List<RunnerEvent> {
        val escapedPath = path.escape()
        val resultFile = resultsDir.resolve(
                escapedPath.toString()
        )

        return parseResultFile(resultFile)
    }

    fun createResultsFileName(): Path {
        val localDate: LocalDateTime = LocalDateTime.now()
        val directoryName: String = localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val fileName: String = localDate.format(FILE_NAME_FORMATTER)

        return Path(listOf(directoryName), fileName, FILE_EXTENSION)
    }

    fun saveEvent(runnerEvent: RunnerEvent,
                  file: Path,
                  resultsDir: JavaPath) {
        val escapedPath = file.escape()
        val resultFile = resultsDir.resolve(
                escapedPath.toString()
        )


        val fileLogLine:String = OBJECT_MAPPER.writeValueAsString(runnerEvent).plus("\n")

        resultFile.parent?.createDirectories()

        Files.write(
                resultFile,
                fileLogLine.toByteArray(charset = Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )
    }

    private fun parseResultFile(javaPath: JavaPath): List<RunnerEvent> {
        try {
            val lines = javaPath.readAllLines()

            return lines.map { OBJECT_MAPPER.readValue<RunnerEvent>(it) }
        } catch (e: Exception) {
            throw RuntimeException("The result file at [${javaPath.toAbsolutePath().normalize()}] couldn't be deserialized", e)
        }
    }

}
