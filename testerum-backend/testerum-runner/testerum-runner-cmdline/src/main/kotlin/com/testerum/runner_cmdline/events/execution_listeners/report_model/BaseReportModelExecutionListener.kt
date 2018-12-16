package com.testerum.runner_cmdline.events.execution_listeners.report_model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.runner.events.execution_listener.BaseExecutionListener
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.events.model.SuiteStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.report_model.FeatureOrTestRunnerReportNode
import com.testerum.runner.report_model.ReportFeature
import com.testerum.runner.report_model.ReportLog
import com.testerum.runner.report_model.ReportStep
import com.testerum.runner.report_model.ReportSuite
import com.testerum.runner.report_model.ReportTest
import java.time.LocalDateTime
import java.util.*
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
abstract class BaseReportModelExecutionListener : BaseExecutionListener() {

    companion object {
        val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    private val eventsStack = ArrayDeque<Any>() // contains both RunnerEvent and RunnerReportNode

    protected abstract fun handleReportModel(reportSuite: ReportSuite);

    final override fun onSuiteStart(event: SuiteStartEvent) {
        eventsStack.addLast(event)
        eventsStack.addLast(
                createLogEvent("Executing test suite")
        )
    }

    final override fun onSuiteEnd(event: SuiteEndEvent) {
        // because ReportSuite should contain log messages that happened after SuiteEndEvent,
        // we are handling this in the stop() method, where we know for sure that there won't me any more events
        eventsStack.addLast(
                createLogEvent("Finished executing test suite")
        )
        eventsStack.addLast(event)
    }

    final override fun onFeatureStart(event: FeatureStartEvent) {
        eventsStack.addLast(event)
        eventsStack.addLast(
                createLogEvent("Executing feature ${event.featureName}")
        )
    }

    final override fun onFeatureEnd(event: FeatureEndEvent) {
        eventsStack.addLast(
                createLogEvent("Finished executing feature ${event.featureName}")
        )

        @Suppress("UnnecessaryVariable")
        val featureEndEvent = event

        val logs = ArrayDeque<ReportLog>()
        var featureStartEvent: FeatureStartEvent? = null
        val children = ArrayDeque<FeatureOrTestRunnerReportNode>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.pollLast()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message
                                )
                        )
                    }
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

        val reportFeature = ReportFeature(
                featureName = featureStartEvent.featureName,
                startTime = featureStartEvent.time,
                endTime = featureEndEvent.time,
                durationMillis = featureEndEvent.durationMillis,
                status = featureEndEvent.status,
                exceptionDetail = featureEndEvent.exceptionDetail,
                logs = logs.toList(),
                children = children.toList()
        )

        eventsStack.addLast(reportFeature)
    }

    final override fun onTestStart(event: TestStartEvent) {
        eventsStack.addLast(event)
        eventsStack.addLast(
                createLogEvent("Executing test ${event.testName}")
        )
    }

    final override fun onTestEnd(event: TestEndEvent) {
        eventsStack.addLast(
                createLogEvent("Finished executing test ${event.testName}")
        )

        @Suppress("UnnecessaryVariable")
        val testEndEvent = event

        val logs = ArrayDeque<ReportLog>()
        var testStartEvent: TestStartEvent? = null
        val children = ArrayDeque<ReportStep>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.pollLast()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message
                                )
                        )
                    }
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

        val reportTest = ReportTest(
                testName = testStartEvent.testName,
                testFilePath = testStartEvent.testFilePath,
                startTime = testStartEvent.time,
                endTime = testEndEvent.time,
                durationMillis = testEndEvent.durationMillis,
                status = testEndEvent.status,
                exceptionDetail = testEndEvent.exceptionDetail,
                logs = logs.toList(),
                children = children.toList()
        )

        eventsStack.addLast(reportTest)
    }

    final override fun onStepStart(event: StepStartEvent) {
        eventsStack.addLast(event)
        eventsStack.addLast(
                createLogEvent("Executing step ${event.stepCall}")
        )
    }

    final override fun onStepEnd(event: StepEndEvent) {
        eventsStack.addLast(
                createLogEvent("Finished executing step ${event.stepCall}")
        )

        @Suppress("UnnecessaryVariable")
        val stepEndEvent = event

        val logs = ArrayDeque<ReportLog>()
        var stepStartEvent: StepStartEvent? = null
        val children = ArrayDeque<ReportStep>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.pollLast()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message
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

        val reportStep = ReportStep(
                stepCall = stepStartEvent.stepCall,
                startTime = stepStartEvent.time,
                endTime = stepEndEvent.time,
                durationMillis = stepEndEvent.durationMillis,
                status = stepEndEvent.status,
                exceptionDetail = stepEndEvent.exceptionDetail,
                logs = logs.toList(),
                children = children.toList()
        )

        eventsStack.addLast(reportStep)
    }

    final override fun onTextLog(event: TextLogEvent) {
        eventsStack.addLast(event)
    }

    override fun stop() {
        val logs = ArrayDeque<ReportLog>()
        var suiteStartEvent: SuiteStartEvent? = null
        var suiteEndEvent: SuiteEndEvent? = null
        val children = ArrayDeque<FeatureOrTestRunnerReportNode>()

        var done = false
        while (!done) {
            val eventFromStack: Any? = eventsStack.pollLast()

            if (eventFromStack == null) {
                done = true
            } else {
                when (eventFromStack) {
                    is TextLogEvent -> {
                        logs.addFirst(
                                ReportLog(
                                        time = eventFromStack.time,
                                        logLevel = eventFromStack.logLevel,
                                        message = eventFromStack.message
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

        val reportSuite = ReportSuite(
                startTime = suiteStartEvent.time,
                endTime = suiteEndEvent.time,
                durationMillis = suiteEndEvent.durationMillis,
                status = suiteEndEvent.status,
                logs = logs.toList(),
                children = children.toList()
        )

        handleReportModel(reportSuite)
    }

    private fun createLogEvent(message: String): TextLogEvent {
        return TextLogEvent(
                time = LocalDateTime.now(),
                eventKey = EventKey.LOG_EVENT_KEY,
                logLevel = LogLevel.INFO,
                message = message
        )
    }

}
