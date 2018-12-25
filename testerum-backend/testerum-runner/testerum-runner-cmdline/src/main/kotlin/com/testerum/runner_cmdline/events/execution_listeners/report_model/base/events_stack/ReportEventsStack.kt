package com.testerum.runner_cmdline.events.execution_listeners.report_model.base.events_stack

import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.report_model.ReportStep
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class ReportEventsStack {

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
        val testName = getCurrentTestName()

        if (featurePath == "") {
            return "$testName-logs"
        } else {
            return "$featurePath/$testName/test-logs"
        }
    }

    private fun getCurrentTestName(): String {
        for (event in stack.descendingIterator()) {
            if (event is TestStartEvent) {
                return event.testName
            }
        }

        throw IllegalArgumentException("there is no test in execution")
    }

    fun computeCurrentStepLogBaseName(): String {
        val featurePath = getCurrentFeaturePath()
        val testName = getCurrentTestName()
        val stepPath = getCurrentStepPath()

        if (featurePath == "") {
            return "$testName-logs"
        } else {
            return "$featurePath/$testName/step-$stepPath-logs"
        }
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

}
