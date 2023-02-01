package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.MapAggregator
import com.testerum.runner.statistics_model.StatsCountByStatus

class StatsCountByStatusAggregator(private val createValueAggregator: () -> Aggregator<Long, Long>) : Aggregator<StatsCountByStatus, StatsCountByStatus> {

    private val aggregator = MapAggregator<ExecutionStatus, Long>(createValueAggregator)
    
    override fun aggregate(event: StatsCountByStatus) {
        aggregator.aggregate(event.countByStatusMap)
    }

    override fun getResult(): StatsCountByStatus {
        return StatsCountByStatus(
                aggregator.getResult()
        )
    }
}
