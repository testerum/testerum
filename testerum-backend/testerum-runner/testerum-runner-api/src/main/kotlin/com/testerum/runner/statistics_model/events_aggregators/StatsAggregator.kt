package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.GroupingAggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.statistics_model.Stats
import com.testerum.runner.statistics_model.StatsAll
import java.time.LocalDate

class StatsAggregator : Aggregator<RunnerEvent, Stats> {

    private val perDayAggregator = GroupingAggregator<RunnerEvent, LocalDate, StatsAll>(
            extractKey = { runnerEvent -> runnerEvent.time.toLocalDate() },
            createValueAggregator = { StatsAllStatsAggregator() }
    )

    override fun aggregate(event: RunnerEvent) {
        perDayAggregator.aggregate(event)
    }

    override fun getResult(): Stats {
        return Stats(
                perDay = perDayAggregator.getResult()
        )
    }

}
