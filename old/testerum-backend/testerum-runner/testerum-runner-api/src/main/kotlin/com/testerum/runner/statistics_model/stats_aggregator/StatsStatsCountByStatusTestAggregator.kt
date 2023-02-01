package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.MapAggregator
import com.testerum.common_stats.sum.LongSumAggregator
import com.testerum.runner.statistics_model.StatsCountByStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class StatsStatsCountByStatusTestAggregator : Aggregator<StatsCountByStatus, StatsCountByStatus> {

    private val aggregator = MapAggregator<ExecutionStatus, Long> {
        LongSumAggregator()
    }

    override fun aggregate(event: StatsCountByStatus) {
        aggregator.aggregate(event.countByStatusMap)
    }

    override fun getResult(): StatsCountByStatus {
        return StatsCountByStatus(
                aggregator.getResult()
        )
    }

}
