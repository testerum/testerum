package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.runner.statistics_model.Stats

class StatsStatsAggregator : Aggregator<Stats, Stats> {

    private val perDayAggregator = StatsStatsPerDayAggregator()

    override fun aggregate(event: Stats) {
        perDayAggregator.aggregate(event.perDay)
    }

    override fun getResult(): Stats {
        return Stats(
                perDay = perDayAggregator.getResult()
        )
    }

}
