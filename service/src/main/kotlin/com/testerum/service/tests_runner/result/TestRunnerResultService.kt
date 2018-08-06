package com.testerum.service.tests_runner.result

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.repository.enums.FileType
import com.testerum.model.run_result.RunnerResultFileInfo
import com.testerum.model.run_result.RunnerResultsDirInfo
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import java.nio.file.Files

class TestRunnerResultService(val fileRepositoryService: FileRepositoryService,
                              val objectMapper: ObjectMapper) {

    fun saveEvent(runnerEvent: RunnerEvent, file: Path) {
        val fileLogLine:String = objectMapper.writeValueAsString(runnerEvent).plus("\n")

        fileRepositoryService.appendToFile(
                KnownPath(file, FileType.RESULT),
                fileLogLine
        )
    }

    fun getResultInfo():List<RunnerResultsDirInfo> {
        val result = mutableListOf<RunnerResultsDirInfo>()
        val allPathsOfResourcesByType: List<Path> = fileRepositoryService.getAllPathsOfResourcesByType(FileType.RESULT)

        for (runResultPath in allPathsOfResourcesByType) {
            val indexOfInResult = result.indexOfFirst { it.directoryName == runResultPath.directories[0] }
            val runnerResultFileInfo: RunnerResultFileInfo = createRunnerResultFileInfo(runResultPath)
            if(indexOfInResult == -1) {
                result.add(
                        RunnerResultsDirInfo(
                                directoryName = runResultPath.directories[0],
                                runnerResultFilesInfo = listOf(runnerResultFileInfo)
                        )
                )
            } else {
                val runResult = result[indexOfInResult]

                val updatedRunResult = runResult.copy(runnerResultFilesInfo = runResult.runnerResultFilesInfo + runnerResultFileInfo)

                result[indexOfInResult] = updatedRunResult
            }
        }

        return result
    }

    private fun createRunnerResultFileInfo(runResultPath: Path): RunnerResultFileInfo {

        val runnerEvents: List<RunnerEvent> = getResultAtPath(runResultPath)

        var status:ExecutionStatus? = null
        var durationMillis: Long? = null
        for (runnerEvent in runnerEvents) {
            if (runnerEvent is SuiteEndEvent) {
                status = runnerEvent.status
                durationMillis = runnerEvent.durationMillis
            }
        }

        return RunnerResultFileInfo(
                path = runResultPath,
                name = runResultPath.fileName!!,
                executionResult = status,
                durationMillis = durationMillis
        )
    }

    fun getResultAtPath(path: Path): List<RunnerEvent> {
        val absoluteResourcePath: java.nio.file.Path? = fileRepositoryService.getExistingResourceAbsolutePath(
                KnownPath(path, FileType.RESULT))?: return emptyList()


        val eventsAsString: MutableList<String> = Files.readAllLines(absoluteResourcePath)

        val resultEvents: MutableList<RunnerEvent> = mutableListOf()
        for (eventAsString in eventsAsString) {
            try {
                val runnerEvent = objectMapper.readValue<RunnerEvent>(eventAsString)
                resultEvents.add(runnerEvent)
            } catch (e: Exception) {
                throw RuntimeException("The result file at [$absoluteResourcePath] couldn't be deserialized", e)
            }
        }

        return resultEvents
    }
}