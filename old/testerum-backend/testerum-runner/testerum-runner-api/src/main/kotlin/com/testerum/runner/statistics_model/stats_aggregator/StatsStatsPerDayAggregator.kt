package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.MapAggregator
import com.testerum.runner.statistics_model.StatsAll
import java.time.LocalDate

class StatsStatsPerDayAggregator : Aggregator<Map<LocalDate, StatsAll>, Map<LocalDate, StatsAll>> {

    private val aggregator = MapAggregator<LocalDate, StatsAll> {
        StatsStatsAllStatsAggregator()
    }

    override fun aggregate(event: Map<LocalDate, StatsAll>) {
        aggregator.aggregate(event)
    }

    override fun getResult(): Map<LocalDate, StatsAll> {
        return aggregator.getResult()
    }
}
