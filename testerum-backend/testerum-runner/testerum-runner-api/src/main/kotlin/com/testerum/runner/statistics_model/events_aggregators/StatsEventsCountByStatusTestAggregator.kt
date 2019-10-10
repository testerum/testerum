package com.testerum.runner.statistics_model.events_aggregators

import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.GroupingAggregator
import com.testerum.common_stats.count.CountAggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.TestEndEvent
import com.testerum.runner.statistics_model.StatsCountByStatus

class StatsEventsCountByStatusTestAggregator : Aggregator<RunnerEvent, StatsCountByStatus> {

    private val aggregator = GroupingAggregator<TestEndEvent, ExecutionStatus, Long>(
            extractKey = { testEndEvent -> testEndEvent.status },
            createValueAggregator = { CountAggregator() }
    )

    override fun aggregate(event: RunnerEvent) {
        if (event is TestEndEvent) {
            aggregator.aggregate(event)
        }
    }

    override fun getResult(): StatsCountByStatus {
        return StatsCountByStatus(
                aggregator.getResult()
        )
    }

}
