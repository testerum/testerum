package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_stats.Aggregator
import com.testerum.runner.events.model.FeatureEndEvent
import com.testerum.runner.events.model.FeatureStartEvent
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.StepEndEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.events.model.TestStartEvent
import com.testerum.runner.statistics_model.StatsCountByStatus
import java.util.*

class StatsCountByStatusPerTagAggregator : Aggregator<RunnerEvent, Map<String, StatsCountByStatus>> {

    private val featureTagsStack = ArrayDeque<Set<String>>()
    private var testTags = emptySet<String>()
    private var stepTags = emptySet<String>()

    private val result = HashMap<String, HashMap<ExecutionStatus, Long>>()

    override fun aggregate(event: RunnerEvent) {
        updateFeatureTagsStack(event)
        updateTestTags(event)
        updateStepTags(event)
        updateResult(event)
    }

    private fun updateFeatureTagsStack(event: RunnerEvent) {
        when (event) {
            is FeatureStartEvent -> featureTagsStack.addLast(TreeSet(event.tags))
            is FeatureEndEvent   -> featureTagsStack.pollLast()
        }
    }

    private fun updateTestTags(event: RunnerEvent) {
        when (event) {
            is TestStartEvent -> testTags = TreeSet(event.tags)
            is TestEndEvent   -> testTags = emptySet()
        }
    }

    private fun updateStepTags(event: RunnerEvent) {
        when (event) {
            is TestStartEvent -> stepTags = TreeSet(event.tags)
            is TestEndEvent   -> stepTags = emptySet()
        }
    }

    private fun updateResult(event: RunnerEvent) {
        var tags = emptySet<String>()
        var status: ExecutionStatus? = null

        when (event) {
            is TestEndEvent -> {
                tags = TreeSet<String>().apply {
                    addAll(featureTagsStack.pollLast() ?: emptySet()) // todo: inherit ALL tags, not only from 
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
