package com.testerum.runner.statistics_model.events_aggregators

import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum.common_stats.Aggregator
import com.testerum.model.step.ComposedStepDef
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.StepStartEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.statistics_model.StatsCountByStatus
import java.util.*

class StatsEventsCountByStatusPerTagAggregator : Aggregator<RunnerEvent, Map<String, StatsCountByStatus>> {

    private val featureTagsStack = ArrayDeque<Set<String>>()
    private var testTags = emptySet<String>()
    private var stepTags = emptySet<String>()

    private val result = HashMap<String, HashMap<ExecutionStatus, Long>>()

    override fun aggregate(event: RunnerEvent) {
        updateFeatureTagsStack(event)
        updateTestTags(event)
        updateStepTags(event)

        updateResult(event)

        cleanupStepTags(event)
        cleanupTestTags(event)
        cleanupFeatureTagsStack(event)
    }

    private fun updateFeatureTagsStack(event: RunnerEvent) {
        if (event is FeatureStartEvent) {
            featureTagsStack.addLast(
                    TreeSet(event.tags)
            )
        }
    }

    private fun cleanupFeatureTagsStack(event: RunnerEvent) {
        if (event is FeatureEndEvent) {
            featureTagsStack.pollLast()
        }
    }

    private fun updateTestTags(event: RunnerEvent) {
        if (event is TestStartEvent) {
            testTags = TreeSet(event.tags)
        }
    }

    private fun cleanupTestTags(event: RunnerEvent) {
        if (event is TestEndEvent) {
            testTags = emptySet()
        }
    }

    private fun updateStepTags(event: RunnerEvent) {
        if (event is StepStartEvent) {
            val stepDef = event.stepCall.stepDef
            if (stepDef is ComposedStepDef) {
                stepTags = TreeSet(stepDef.tags)
            }
        }
    }

    private fun cleanupStepTags(event: RunnerEvent) {
        if (event is StepEndEvent) {
            stepTags = emptySet()
        }
    }

    private fun updateResult(event: RunnerEvent) {
        var tags = emptySet<String>()
        var status: ExecutionStatus? = null

        when (event) {
            is TestEndEvent -> {
                tags = TreeSet<String>().apply {
                    addAll(featureTagsStack.pollLast() ?: emptySet())
                    addAll(testTags)
                }

                status = event.status
            }
            is StepEndEvent -> {
                tags = stepTags
                status = event.status
            }
        }

        if (status == null) {
            return
        }
        if (tags.isEmpty()) {
            return
        }

        for (tag in tags) {
            val countByStatus: HashMap<ExecutionStatus, Long> = result.computeIfAbsent(tag) { HashMap() }

            countByStatus[status] = (countByStatus[status] ?: 0) + 1
        }
    }

    override fun getResult(): Map<String, StatsCountByStatus> = result.mapValues { entry -> StatsCountByStatus(entry.value) }

}
