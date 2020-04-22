package com.testerum.runner.statistics_model.stats_aggregator

import com.testerum.common_stats.Aggregator
import com.testerum.common_stats.MapAggregator
import com.testerum.common_stats.sum.LongSumAggregator
import com.testerum.runner.statistics_model.StatsCountByStatus
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import java.math.BigDecimal
import java.math.RoundingMode

class StatsStatsCountByStatusPerTagAggregator : Aggregator<Map<String, StatsCountByStatus>, Map<String, StatsCountByStatus>> {

    private var lastEvent: Map<String, StatsCountByStatus>? = null

    private val aggregator = MapAggregator<String, StatsCountByStatus>(
            createValueAggregator = {
                StatsCountByStatusAggregator {
                    LongSumAggregator()
                }
            }
    )

    override fun aggregate(event: Map<String, StatsCountByStatus>) {
        aggregator.aggregate(event)
        lastEvent = event
    }

    override fun getResult(): Map<String, StatsCountByStatus> {
        val lastEvent = this.lastEvent
                ?: return emptyMap() // no events were passed for aggregation

        val result = mutableMapOf<String, StatsCountByStatus>()

        val countsPerTagPerStatus = aggregator.getResult()

        for ((tag, countByStatus) in countsPerTagPerStatus) { // for each tag
            val lastCount = lastEvent[tag]
                    ?: StatsCountByStatus(emptyMap())

            val allExecutionsTotal = countByStatus.totalCount()
            val lastExecutionTotal = lastCount.totalCount()

            if (allExecutionsTotal == 0L) {
                result[tag] = StatsCountByStatus(emptyMap())
            } else {
                val adjustedCountByStatusMap = mutableMapOf<ExecutionStatus, Long>()
                val factor = BigDecimal.valueOf(lastExecutionTotal)
                        .divide(BigDecimal.valueOf(allExecutionsTotal), 10, RoundingMode.HALF_EVEN)

                for (status in ExecutionStatus.values()) {
                    adjustedCountByStatusMap[status] = BigDecimal.valueOf(countByStatus.getCount(status))
                            .multiply(factor).setScale(0, RoundingMode.HALF_EVEN).toLong()
                }

                val adjustedStatsTotal = adjustedCountByStatusMap.totalCount()
                val executionWithMaxCount = adjustedCountByStatusMap.maxBy { (_, count) -> count }!!.key
                adjustedCountByStatusMap[executionWithMaxCount] = adjustedCountByStatusMap[executionWithMaxCount]!! + (lastExecutionTotal - adjustedStatsTotal)

                result[tag] = StatsCountByStatus(adjustedCountByStatusMap)
            }
        }

        return result
    }

    private fun Map<ExecutionStatus, Long>.totalCount(): Long = this.values.sum()
    private fun StatsCountByStatus.totalCount(): Long = this.countByStatusMap.totalCount()

}
