package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.GroupingAggregator
import com.testerum.common_stats.count.CountAggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.ScenarioEndEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.statistics_model.StatsCountByStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class StatsEventsCountByStatusTestAggregator : Aggregator<RunnerEvent, StatsCountByStatus> {

    private val aggregator = GroupingAggregator<RunnerEvent, ExecutionStatus, Long>(
            extractKey = { event ->
                if (event is TestEndEvent) {
                    event.status
                } else if (event is ScenarioEndEvent) {
                    event.status
                } else {
                    throw IllegalArgumentException("unexpected event type [${event::class.java}]")
                }
            },
            createValueAggregator = { CountAggregator() }
    )

    override fun aggregate(event: RunnerEvent) {
        if (event is TestEndEvent || event is ScenarioEndEvent) {
            aggregator.aggregate(event)
        }
    }

    override fun getResult(): StatsCountByStatus {
        return StatsCountByStatus(
                aggregator.getResult()
        )
    }

}
