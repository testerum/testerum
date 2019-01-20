package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.avg.AvgAvgAggregator
import com.testerum.runner.statistics_model.StatsAll

class StatsStatsAllStatsAggregator : Aggregator<StatsAll, StatsAll> {

    private val statusAggregator = StatsStatsStatusStatsAggregator()
    private val suiteAvgDurationMillisAggregator = AvgAvgAggregator()

    override fun aggregate(event: StatsAll) {
        statusAggregator.aggregate(event.status)
        suiteAvgDurationMillisAggregator.aggregate(event.suiteAvgDurationMillis)
    }

    override fun getResult(): StatsAll {
        return StatsAll(
                status = statusAggregator.getResult(),
                suiteAvgDurationMillis = suiteAvgDurationMillisAggregator.getResult()
        )
    }
}
