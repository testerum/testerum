package com.testerum.runner.statistics_model.events_aggregators

import com.testerum.common_stats.Aggregator
import com.testerum.runner.events.model.RunnerEvent
import com.testerum.runner.statistics_model.StatsStatus

class StatsStatusStatsAggregator : Aggregator<RunnerEvent, StatsStatus> {

    private val suiteAggregator = StatsCountByStatusSuiteAggregator()
    private val testAggregator = StatsCountByStatusTestAggregator()
    private val perTagAggregator = StatsCountByStatusPerTagAggregator()

    override fun aggregate(event: RunnerEvent) {
        suiteAggregator.aggregate(event)
        testAggregator.aggregate(event)
        perTagAggregator.aggregate(event)
    }

    override fun getResult(): StatsStatus {
        return StatsStatus(
                suiteCount = suiteAggregator.getResult(),
                testAvg = testAggregator.getResult(),
                perTagAvg = perTagAggregator.getResult()
        )
    }

}
