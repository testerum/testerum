package com.testerum.report_generators.reports.utils.events_stack

import com.testerum.runner.events.model.*
import com.testerum.runner.report_model.ReportStep
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList

class ExecutionEventsStack {

    private val stack = ArrayDeque<Any>() // contains both RunnerEvent and RunnerReportNode

    fun push(item: Any) {
        stack.addLast(item)
    }

    fun popOrNull(): Any? {
        return stack.pollLast()
    }

    fun computeCurrentFeatureLogBaseName(): String {
        return "${getCurrentFeaturePath()}/feature-logs"
    }

    private fun getCurrentFeaturePath(): String {
        val fullFeaturePath = StringBuilder()

        for (event in stack) {
            if (event !is FeatureStartEvent) {
                continue
            }

            if (!fullFeaturePath.isEmpty()) {
                fullFeaturePath.append("/")
            }
            fullFeaturePath.append(event.featureName)
        }

        return fullFeaturePath.toString()
    }

    fun computeCurrentTestLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testFileName = getCurrentTestFileName()

        if (featurePath == "") {
            return "$testFileName-logs"
        } else {
            return "$featurePath/$testFileName/test-logs"
        }
    }

    fun computeCurrentHooksLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testFileName = getCurrentTestFileName()
        val hooksName = getCurrentHooksName()

        val result = StringBuilder()
        if(featurePath.isNotEmpty()) result.append("$featurePath/")
        if(testFileName.isNullOrEmpty()) result.append("$testFileName/")
        if(hooksName.isNullOrEmpty()) result.append("$hooksName-logs")

        return result.toString()
    }

    fun computeCurrentParametrizedTestLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testFileName = getCurrentTestFileName()

        if (featurePath == "") {
            return "$testFileName-logs"
        } else {
            return "$featurePath/$testFileName/test-logs"
        }
    }

    fun computeCurrentScenarioLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testFileName = getCurrentTestFileName()
        val scenarioIndex = getCurrentScenarioIndex()

        if (featurePath == "") {
            return "$testFileName-logs-$scenarioIndex"
        } else {
            return "$featurePath/$testFileName/test-logs-$scenarioIndex"
        }
    }

    private fun getCurrentTestFileName(): String? {
        for (event in stack.descendingIterator()) {
            if (event is TestStartEvent) {
                return event.testFilePath.fileName!!
            } else if (event is ParametrizedTestStartEvent) {
                return event.testFilePath.fileName!!
            }
        }

        return null
    }

    private fun getCurrentScenarioIndex(): Int {
        for (event in stack.descendingIterator()) {
            if (event is ScenarioStartEvent) {
                return event.scenarioIndex
            }
        }

        throw IllegalArgumentException("there is no scenario in execution")
    }

    private fun getCurrentHooksName(): String? {
        for (event in stack.descendingIterator()) {
            if (event is HooksStartEvent) {
                return event.hookPhase.toString()
            }
        }

        return null
    }

    fun computeCurrentStepLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testName = getCurrentTestFileName()
        val stepPath = getCurrentStepPath()

        val result = StringBuilder()
        if (featurePath.isNotEmpty()) {
            result.append("$featurePath/")
        }

        if (testName != null) {
            result.append("$testName/")
        }

        result.append("step-$stepPath-logs")

        return result.toString()
    }

    private fun getCurrentStepPath(): String {
        // step call number (starting at 1) within its parent
        // using AtomicInteger rather than Int, to be able to mutate it
        val stepCallNumberStack = ArrayDeque<AtomicInteger>()

        loop@ for (event in stack.descendingIterator()) {
            when (event) {
                is StepStartEvent -> {
                    // step in execution
                    stepCallNumberStack.addFirst(AtomicInteger(1))
                }
                is ReportStep -> {
                    // previous step (finished) in the same parent
                    stepCallNumberStack.peekFirst()?.incrementAndGet()
                }
                is TestStartEvent -> break@loop
            }
        }

        return stepCallNumberStack.joinToString(separator = "-")
    }

    fun <T, R> peek(untilItemType: Class<T>? = null,
                    desiredItemType: Class<R>? = null): List<R>  {
        val result = ArrayList<R>()

        for (event in stack.descendingIterator()) {
            if (untilItemType != null && untilItemType.isInstance(event)) {
                break
            }

            val shouldAdd = (desiredItemType == null)
                    || ( desiredItemType.isInstance(event))

            @Suppress("UNCHECKED_CAST") // safe because of the "isInstance()" check above
            if (shouldAdd) {
                result += event as R
            }
        }

        result.reverse()

        return result
    }

}
