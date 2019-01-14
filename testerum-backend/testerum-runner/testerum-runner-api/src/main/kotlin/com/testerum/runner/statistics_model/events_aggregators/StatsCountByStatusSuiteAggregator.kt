package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.api.test_context.ExecutionStatus
import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.GroupingAggregator
import com.testerum.common_stats.count.CountAggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.statistics_model.StatsCountByStatus

class StatsCountByStatusSuiteAggregator : Aggregator<RunnerEvent, StatsCountByStatus> {

    private val aggregator = GroupingAggregator<SuiteEndEvent, ExecutionStatus, Long>(
            extractKey = { suiteEndEvent -> suiteEndEvent.status },
            createValueAggregator = { CountAggregator() }
    )

    override fun aggregate(event: RunnerEvent) {
        if (event is SuiteEndEvent) {
            aggregator.aggregate(event)
        }
    }

    override fun getResult(): StatsCountByStatus {
        return StatsCountByStatus(
                aggregator.getResult()
        )
    }

}
