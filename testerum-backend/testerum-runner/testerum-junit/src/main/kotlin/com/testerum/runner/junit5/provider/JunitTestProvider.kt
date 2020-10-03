package com.testerum.runner.junit5.provider

import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.model.tests_finder.TestPath
import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner.events.execution_listener.ExecutionListener
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.junit5.logger.JUnitEventLogger
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.events.execution_listeners.junit.JUnitExecutionListener
import com.testerum.runner_cmdline.module_di.RunnerModuleBootstrapper
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerFeatureOrTest
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerParametrizedTest
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.runner_cmdline.runner_tree.nodes.test.RunnerTest
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import org.junit.jupiter.api.DynamicContainer.dynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.fail
import java.nio.file.Path
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingDeque
import kotlin.concurrent.thread

//We expect the tests in JUnit to be executed in the same order as in Testerum,
//and if this is not the case, this class will do the wrong thing without warning.
class JunitTestProvider(repositoryDirectory: Path,
                        variablesEnvironment: String? = null,
                        variableOverrides: Map<String, String> = emptyMap(),
                        settingsFile: Path? = null,
                        settingOverrides: Map<String, String> = emptyMap(),
                        testPaths: List<TestPath> = emptyList(),
                        tagsToInclude: List<String> = emptyList(),
                        tagsToExclude: List<String> = emptyList()
) {

    private val cmdlineParams: CmdlineParams = CmdlineParams(
            verbose = false,
            repositoryDirectory = repositoryDirectory,
            variablesEnvironment = variablesEnvironment,
            variableOverrides = variableOverrides,
            settingsFile = settingsFile,
            settingOverrides = settingOverrides,
            testPaths = testPaths,
            tagsToInclude = tagsToInclude,
            tagsToExclude = tagsToExclude,
            reportsWithProperties = listOf("JUNIT"),
            managedReportsDir = null,
            executionName = null
    )

    private val bootstrapper: RunnerModuleBootstrapper = RunnerModuleBootstrapper(cmdlineParams, StopWatch.start())

    private lateinit var testSuiteToBeExecuted: RunnerSuite
    private val loggingListener = JUnitEventLogger()

    private lateinit var eventQueue: LinkedBlockingDeque<RunnerEvent>
    private val eventQueueInitialized = CountDownLatch(1)

    private var isTestStarted = false

    fun getTesterumTests(): List<DynamicNode> {
        ConsoleOutputCapturer.startCapture("junit-TesterumRunner")

        testSuiteToBeExecuted = bootstrapper.runnerModuleFactory.runnerApplication.getRunnerSuiteToBeExecuted(cmdlineParams)

        val junitExecutionTree = testSuiteToBeExecuted.featuresOrTests.map { defineNode(it) }


        if (junitExecutionTree.isEmpty()) {
            println("WARNING: No tests were found!")
            return junitExecutionTree
        }

        thread(start = true) {
            executeTesterumTests()
        }

        handleTestInitializingEvents()

        return junitExecutionTree
    }

    private fun defineNode(runnerNode: RunnerFeatureOrTest): DynamicNode {
        return when (runnerNode) {
            is RunnerFeature -> dynamicContainer(runnerNode.featureName, runnerNode.featuresOrTests.map { defineNode(it) })
            is RunnerParametrizedTest -> dynamicContainer(runnerNode.test.name, runnerNode.scenarios.map { defineNode(it) })
            is RunnerScenario -> dynamicTest(runnerNode.scenarioName, { handleTestInfo() })
            is RunnerTest -> dynamicTest(runnerNode.test.name, { handleTestInfo() })
            else -> throw RuntimeException("Unhandled ${runnerNode.javaClass.name} case")
        }
    }

    private fun executeTesterumTests() {
        val executionListeners = bootstrapper.runnerListenersModuleFactory.executionListenerFinder.executionListeners
        val jUnitExecutionListener: ExecutionListener = executionListeners.find { listener -> listener is JUnitExecutionListener }
                ?: throw RuntimeException("JUnitExecutionListener is not registered")

        eventQueue = (jUnitExecutionListener as JUnitExecutionListener).eventQueue
        eventQueueInitialized.countDown()

        bootstrapper.context.use {
            try {
                bootstrapper.runnerModuleFactory.runnerApplication.jUnitExecute(cmdlineParams, testSuiteToBeExecuted)
            } finally {
                val remainingConsoleCapturedText: String = ConsoleOutputCapturer.drainCapturedText()

                ConsoleOutputCapturer.stopCapture()

                try {
                    for (line in remainingConsoleCapturedText.lines()) {
                        bootstrapper.runnerModuleFactory.eventsService.logEvent(
                                TextLogEvent(
                                        time = LocalDateTime.now(),
                                        eventKey = EventKey.LOG_EVENT_KEY,
                                        logLevel = LogLevel.INFO,
                                        message = line,
                                        exceptionDetail = null
                                )
                        )
                    }
                } catch (e: Exception) {
                    // if we failed to notify listeners, show output to console, so we don't lose it
                    println("An error occurred while trying to notify event listeners of remaining logs:")
                    e.printStackTrace()

                    println()
                    println("Remaining logs:")
                    println("----------------------------------------(start)----------------------------------------")
                    println(remainingConsoleCapturedText)
                    println("----------------------------------------( end )----------------------------------------")
                }
            }
        }
    }

    private fun handleTestInitializingEvents() {
        eventQueueInitialized.await()

        while (true) {
            val event = eventQueue.take()

            if (event is TestStartEvent) {
                eventQueue.putFirst(event)

                isTestStarted = true
                return
            }

            logEvent(event)
        }
    }

    private fun handleTestInfo() {
        eventQueueInitialized.await()

        while (true) {
            val event = eventQueue.take()

            if (event is TestStartEvent) {
                isTestStarted = true
            }

            if (isTestStarted) {
                logEvent(event)
            }

            if (event is TestEndEvent) {
                isTestStarted = false

                when (event.status) {
                    ExecutionStatus.FAILED -> fail("Test FAILED")
                    ExecutionStatus.UNDEFINED -> fail("Test is UNDEFINED")
                    ExecutionStatus.DISABLED -> println("Test is DISABLED")
                    ExecutionStatus.SKIPPED -> println("Test is marked to be SKIPPED")
                    ExecutionStatus.PASSED -> println("Test is PASSED")
                }

                return
            }
        }
    }

    private fun logEvent(event: RunnerEvent) {
        loggingListener.onEvent(event)
    }
}
