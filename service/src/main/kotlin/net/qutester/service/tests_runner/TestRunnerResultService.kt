package net.qutester.service.tests_runner

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.api.test_context.ExecutionStatus
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType
import net.qutester.model.run_result.RunnerResultFileInfo
import net.qutester.model.run_result.RunnerResultsDirInfo
import net.qutester.service.tests_runner.event_bus.TestRunnerEventBus
import net.qutester.service.tests_runner.event_bus.TestRunnerEventListener
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

class TestRunnerResultService(val fileRepositoryService: FileRepositoryService,
                              val objectMapper: ObjectMapper,
                              testRunnerEventBus: TestRunnerEventBus) : TestRunnerEventListener {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TestRunnerResultService::class.java)
    }

    private val FILE_NAME_FORMATER: DateTimeFormatter = DateTimeFormatter.ofPattern("HH-mm-ss_SSS")
    private var currentResultPath: Path? = null

    init {
        testRunnerEventBus.addEventListener(this)
    }

    override fun testSuiteEvent(runnerEvent: RunnerEvent) {
        if (runnerEvent is SuiteStartEvent) {
            currentResultPath = createResultsFile()
        }

        saveEvent(runnerEvent)

        if (runnerEvent is SuiteEndEvent) {
            currentResultPath = null
        }
    }

    private fun createResultsFile(): Path {
        val localDate: LocalDateTime = LocalDateTime.now();
        val directoryName: String = localDate.format(ISO_LOCAL_DATE);
        val fileName: String = localDate.format(FILE_NAME_FORMATER)

        val resultFilePath = Path(listOf(directoryName), fileName, FileType.RESULT.fileExtension)
        fileRepositoryService.createFile(
                KnownPath(resultFilePath, FileType.RESULT)
        )
        return resultFilePath
    }

    private fun saveEvent(runnerEvent: RunnerEvent) {
        if (currentResultPath == null) {
            LOGGER.error("A RunnerEvent [${runnerEvent.javaClass}] was triggered without the Report file being initialized")
            return;
        }

        val fileLogLine:String = objectMapper.writeValueAsString(runnerEvent).plus("\n")

        fileRepositoryService.appendToFile(
                KnownPath(currentResultPath!!, FileType.RESULT),
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
                val runResult = result.get(indexOfInResult)

                val updatedRunResult = runResult.copy(runnerResultFilesInfo = runResult.runnerResultFilesInfo + runnerResultFileInfo)

                result[indexOfInResult] = updatedRunResult
            }
        }

        return result
    }

    private fun createRunnerResultFileInfo(runResultPath: Path): RunnerResultFileInfo {

        val runnerEvents: List<RunnerEvent> = getResultAtPath(runResultPath)

        var status:ExecutionStatus? = null;
        var durationMilis: Long? = null;
        for (runnerEvent in runnerEvents) {
            if (runnerEvent is SuiteEndEvent) {
                status = runnerEvent.status
                durationMilis = runnerEvent.durationMillis
            }
        }

        return RunnerResultFileInfo(
                path = runResultPath,
                name = runResultPath.fileName!!,
                executionResult = status,
                durationMillis = durationMilis
        )
    }

    fun getResultAtPath(path: Path): List<RunnerEvent> {
        val absoluteResourcePath: java.nio.file.Path? = fileRepositoryService.getExistingResourceAbsolutePath(
                KnownPath(path, FileType.RESULT))?: return emptyList()


        val eventsAsString: MutableList<String> = Files.readAllLines(absoluteResourcePath)

        val resultEvents: MutableList<RunnerEvent> = mutableListOf();
        for (eventAsString in eventsAsString) {
            try {
                val runnerEvent = objectMapper.readValue<RunnerEvent>(eventAsString)
                resultEvents.add(runnerEvent)
            } catch (e: Exception) {
                throw RuntimeException("The result file at [${absoluteResourcePath}] couldn't be deserialized", e)
            }
        }

        return resultEvents
    }
}