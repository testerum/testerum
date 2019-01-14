package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.avg.AvgAggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.events.model.SuiteEndEvent
import com.testerum.runner.statistics_model.StatsAll

class StatsAllStatsAggregator : Aggregator<RunnerEvent, StatsAll> {

    private val statusAggregator = StatsStatusStatsAggregator()
    private val suiteAvgDurationMillisAggregator = AvgAggregator()

    override fun aggregate(event: RunnerEvent) {
        statusAggregator.aggregate(event)

        if (event is SuiteEndEvent) {
            suiteAvgDurationMillisAggregator.aggregate(event.durationMillis)
        }
    }

    override fun getResult(): StatsAll {
        return StatsAll(
                status = statusAggregator.getResult(),
                suiteAvgDurationMillis = suiteAvgDurationMillisAggregator.getResult()
        )
    }

}
