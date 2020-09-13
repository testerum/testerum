package com.testerum.report_generators.reports.report_model.base

import com.fasterxml.jackson.databind.ObjectWriter
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.writeText
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.ParametrizedTestEndEvent
import com.testerum.runner.events.model.ParametrizedTestStartEvent
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.ScenarioStartEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.report_model.FeatureOrTestRunnerReportNode
import com.testerum.runner.report_model.ReportLog
import com.testerum.runner.report_model.ReportScenario
import com.testerum.runner.report_model.ReportStep
import com.testerum.report_generators.reports.report_model.base.logger.ReportToFileLoggerStack
import com.testerum.report_generators.reports.report_model.base.mapper.ReportFeatureMapper
import com.testerum.report_generators.reports.report_model.base.mapper.ReportParametrizedTestMapper
import com.testerum.report_generators.reports.report_model.base.mapper.ReportScenarioMapper
import com.testerum.report_generators.reports.report_model.base.mapper.ReportStepMapper
import com.testerum.report_generators.reports.report_model.base.mapper.ReportSuiteMapper
import com.testerum.report_generators.reports.report_model.base.mapper.ReportTestMapper
import com.testerum.report_generators.reports.report_model.base.mapper.StepDefsByMinId
import com.testerum.report_generators.reports.utils.EXECUTION_LISTENERS_OBJECT_MAPPER
import com.testerum.report_generators.reports.utils.events_stack.ExecutionEventsStack
import java.util.ArrayDeque
import javax.annotation.concurrent.NotThreadSafe
import java.nio.file.Path as JavaPath

@NotThreadSafe
abstract class BaseReportModelExecutionListener : BaseExecutionListener() {

    companion object {
        private val MODEL_DESTINATION_FILE_NAME = "model.json"
        private val LOG_TEXT_EXTENSION = "txt"
        private val LOG_MODEL_EXTENSION = "js"
    }

    private val eventsStack = ExecutionEventsStack() // contains both RunnerEvent and RunnerReportNode
    private val loggerStack = ReportToFileLoggerStack()
    private val stepDefsByMinId = StepDefsByMinId()

    protected abstract val destinationDirectory: JavaPath
    protected abstract val formatted: Boolean
    protected open fun afterModelSavedToFile() {}

    private fun getTextLogsDirectory(): JavaPath = destinationDirectory.resolve("logs/text")
    private fun getModelLogsDirectory(): JavaPath = destinationDirectory.resolve("logs/model")

    override fun start() {
        // Starting to record suite logs here, rather than in onSuiteStart(),
        // because we want to capture also the logs before the suite started
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("suite-logs.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("suite-logs.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onSuiteStart(event: SuiteStartEvent) {
        eventsStack.push(event)
    }

    final override fun onSuiteEnd(event: SuiteEndEvent) {
        // Because ReportSuite should contain log messages that happened after SuiteEndEvent,
        // we are handling postponing the handling of this event until the stop() method,
        // where we know for sure that there won't me any more events.
        eventsStack.push(event)
    }

    final override fun onFeatureStart(event: FeatureStartEvent) {
        eventsStack.push(event)

        val logBaseName = eventsStack.computeCurrentFeatureLogBaseName()
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("$logBaseName.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("$logBaseName.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onFeatureEnd(event: FeatureEndEvent) {
        @Suppress("UnnecessaryVariable")
        val featureEndEvent = event

        var featureStartEvent: FeatureStartEvent? = null
        val children = ArrayDeque<FeatureOrTestRunnerReportNode>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is FeatureStartEvent -> {
                        featureStartEvent = eventFromStack
                        done = true
                    }
                    is FeatureOrTestRunnerReportNode -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (featureStartEvent == null) {
            throw IllegalStateException(
                    "Got ${FeatureEndEvent::class.simpleName} [$featureEndEvent]" +
                    " without a corresponding previous ${FeatureStartEvent::class.simpleName}"
            )
        }

        val featureLogger = loggerStack.pop()

        val reportFeature = ReportFeatureMapper.mapReportFeature(
                startEvent = featureStartEvent,
                endEvent = featureEndEvent,
                destinationDirectory = destinationDirectory,
                featureLogger = featureLogger,
                children = children.toList()
        )

        eventsStack.push(reportFeature)
    }

    final override fun onParametrizedTestStart(event: ParametrizedTestStartEvent) {
        eventsStack.push(event)

        val logBaseName = eventsStack.computeCurrentParametrizedTestLogBaseName()
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("$logBaseName.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("$logBaseName.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onParametrizedTestEnd(event: ParametrizedTestEndEvent) {
        @Suppress("UnnecessaryVariable")
        val parametrizedTestEndEvent = event

        var parametrizedTestStartEvent: ParametrizedTestStartEvent? = null
        val children = ArrayDeque<ReportScenario>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is ParametrizedTestStartEvent -> {
                        parametrizedTestStartEvent = eventFromStack
                        done = true
                    }
                    is ReportScenario -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (parametrizedTestStartEvent == null) {
            throw IllegalStateException(
                    "Got ${ParametrizedTestEndEvent::class.simpleName} [$parametrizedTestEndEvent]" +
                    " without a corresponding previous ${ParametrizedTestStartEvent::class.simpleName}"
            )
        }

        val testLogger = loggerStack.pop()

        val reportParametrizedTest = ReportParametrizedTestMapper.mapReportParametrizedTest(
                startEvent = parametrizedTestStartEvent,
                endEvent = parametrizedTestEndEvent,
                destinationDirectory = destinationDirectory,
                testLogger = testLogger,
                children = children.toList()
        )

        eventsStack.push(reportParametrizedTest)
    }

    final override fun onScenarioStart(event: ScenarioStartEvent) {
        eventsStack.push(event)

        val logBaseName = eventsStack.computeCurrentScenarioLogBaseName()
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("$logBaseName.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("$logBaseName.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onScenarioEnd(event: ScenarioEndEvent) {
        @Suppress("UnnecessaryVariable")
        val scenarioEndEvent = event

        var scenarioStartEvent: ScenarioStartEvent? = null
        val children = ArrayDeque<ReportStep>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is ScenarioStartEvent -> {
                        scenarioStartEvent = eventFromStack
                        done = true
                    }
                    is ReportStep -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (scenarioStartEvent == null) {
            throw IllegalStateException(
                    "Got ${ScenarioEndEvent::class.simpleName} [$scenarioEndEvent]" +
                    " without a corresponding previous ${ScenarioStartEvent::class.simpleName}"
            )
        }

        val testLogger = loggerStack.pop()

        val reportScenario = ReportScenarioMapper.mapReportScenario(
                startEvent = scenarioStartEvent,
                endEvent = scenarioEndEvent,
                destinationDirectory = destinationDirectory,
                testLogger = testLogger,
                children = children.toList()
        )

        eventsStack.push(reportScenario)
    }

    final override fun onTestStart(event: TestStartEvent) {
        eventsStack.push(event)

        val logBaseName = eventsStack.computeCurrentTestLogBaseName()
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("$logBaseName.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("$logBaseName.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onTestEnd(event: TestEndEvent) {
        @Suppress("UnnecessaryVariable")
        val testEndEvent = event

        var testStartEvent: TestStartEvent? = null
        val children = ArrayDeque<ReportStep>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TestStartEvent -> {
                        testStartEvent = eventFromStack
                        done = true
                    }
                    is ReportStep -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (testStartEvent == null) {
            throw IllegalStateException(
                    "Got ${TestEndEvent::class.simpleName} [$testEndEvent]" +
                    " without a corresponding previous ${TestStartEvent::class.simpleName}"
            )
        }

        val testLogger = loggerStack.pop()

        val reportTest = ReportTestMapper.mapReportTest(
                startEvent = testStartEvent,
                endEvent = testEndEvent,
                destinationDirectory = destinationDirectory,
                testLogger = testLogger,
                children = children.toList()
        )

        eventsStack.push(reportTest)
    }

    final override fun onStepStart(event: StepStartEvent) {
        eventsStack.push(event)

        val logBaseName = eventsStack.computeCurrentStepLogBaseName()
        loggerStack.push(
                textFilePath = getTextLogsDirectory().resolve("$logBaseName.$LOG_TEXT_EXTENSION"),
                modelFilePath = getModelLogsDirectory().resolve("$logBaseName.$LOG_MODEL_EXTENSION")
        )
    }

    final override fun onStepEnd(event: StepEndEvent) {
        @Suppress("UnnecessaryVariable")
        val stepEndEvent = event

        val logs = ArrayDeque<ReportLog>()
        var stepStartEvent: StepStartEvent? = null
        val children = ArrayDeque<ReportStep>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message,
                                        exceptionDetail = eventFromStack.exceptionDetail
                                )
                        )
                    }
                    is StepStartEvent -> {
                        stepStartEvent = eventFromStack
                        done = true
                    }
                    is ReportStep -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (stepStartEvent == null) {
            throw IllegalStateException(
                    "Got ${StepEndEvent::class.simpleName} [$stepEndEvent]" +
                    " without a corresponding previous ${StepStartEvent::class.simpleName}"
            )
        }

        val stepLogger = loggerStack.pop()

        val reportStep = ReportStepMapper.mapReportStep(
                startEvent = stepStartEvent,
                endEvent = stepEndEvent,
                destinationDirectory = destinationDirectory,
                stepLogger = stepLogger,
                children = children.toList(),
                stepDefsByMinId = stepDefsByMinId
        )

        eventsStack.push(reportStep)
    }

    final override fun onTextLog(event: TextLogEvent) {
        loggerStack.log(
                ReportLog(
                        time = event.time,
                        logLevel = event.logLevel,
                        message = event.message,
                        exceptionDetail = event.exceptionDetail
                )
        )
    }

    override fun stop() {
        val logs = ArrayDeque<ReportLog>()
        var suiteStartEvent: SuiteStartEvent? = null
        var suiteEndEvent: SuiteEndEvent? = null
        val children = ArrayDeque<FeatureOrTestRunnerReportNode>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.popOrNull()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message,
                                        exceptionDetail = eventFromStack.exceptionDetail
                                )
                        )
                    }
                    is SuiteStartEvent -> {
                        suiteStartEvent = eventFromStack
                    }
                    is SuiteEndEvent -> {
                        suiteEndEvent = eventFromStack
                    }
                    is FeatureOrTestRunnerReportNode -> {
                        children.addFirst(eventFromStack)
                    }
                }
            }
        }

        if (suiteStartEvent == null) {
            throw IllegalStateException("Could not find ${SuiteStartEvent::class.simpleName}")
        }
        if (suiteEndEvent == null) {
            throw IllegalStateException("Could not find ${SuiteEndEvent::class.simpleName}")
        }

        val suiteLogger = loggerStack.pop()

        val reportSuite = ReportSuiteMapper.mapReportSuite(
                startEvent = suiteStartEvent,
                endEvent = suiteEndEvent,
                destinationDirectory = destinationDirectory,
                suiteLogger = suiteLogger,
                children = children.toList(),
                stepDefsByMinId = stepDefsByMinId
        )

        // serialize model
        val objectWriter: ObjectWriter = if (formatted) {
            EXECUTION_LISTENERS_OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
        } else {
            EXECUTION_LISTENERS_OBJECT_MAPPER.writer()
        }
        val serializedModel = objectWriter.writeValueAsString(reportSuite)

        // write data file
        destinationDirectory.createDirectories()
        destinationDirectory.resolve(MODEL_DESTINATION_FILE_NAME).writeText(serializedModel)

        afterModelSavedToFile()
    }

}
