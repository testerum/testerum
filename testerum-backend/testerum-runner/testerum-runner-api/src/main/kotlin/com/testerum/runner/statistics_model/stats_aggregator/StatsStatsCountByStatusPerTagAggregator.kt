package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.MapAggregator
import com.testerum.common_stats.sum.LongSumAggregator
import com.testerum.runner.statistics_model.StatsCountByStatus

class StatsStatsCountByStatusPerTagAggregator : Aggregator<Map<String, StatsCountByStatus>, Map<String, StatsCountByStatus>> {

    private val aggregator = MapAggregator<String, StatsCountByStatus>(
            createValueAggregator = {
                StatsCountByStatusAggregator {
                    LongSumAggregator()
                }
            }
    )

    override fun aggregate(event: Map<String, StatsCountByStatus>) {
        aggregator.aggregate(event)
    }

    override fun getResult(): Map<String, StatsCountByStatus> {
        return aggregator.getResult()
    }

}
