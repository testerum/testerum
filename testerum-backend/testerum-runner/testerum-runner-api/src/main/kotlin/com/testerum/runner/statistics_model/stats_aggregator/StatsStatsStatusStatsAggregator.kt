package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.runner.statistics_model.StatsStatus

class StatsStatsStatusStatsAggregator : Aggregator<StatsStatus, StatsStatus> {

    private val suiteAggregator = StatsStatsCountByStatusSuiteAggregator()
    private val testAggregator = StatsStatsCountByStatusTestAggregator()
    private val perTagAggregator = StatsStatsCountByStatusPerTagAggregator()

    override fun aggregate(event: StatsStatus) {
        suiteAggregator.aggregate(event.suiteCount)
        testAggregator.aggregate(event.testAvg)
        perTagAggregator.aggregate(event.perTagAvg)
    }

    override fun getResult(): StatsStatus {
        return StatsStatus(
                suiteCount = suiteAggregator.getResult(),
                testAvg = testAggregator.getResult(),
                perTagAvg = perTagAggregator.getResult()
        )
    }

}
