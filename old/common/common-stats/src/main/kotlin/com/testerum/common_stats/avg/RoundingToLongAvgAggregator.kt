package com.testerum.common_stats.avg

import com.testerum.common_stats.Aggregator
import kotlin.math.round

class RoundingToLongAvgAggregator : Aggregator<Long, Long> {

    private val avgAggregator = AvgAggregator()

    override fun aggregate(event: Long) {
        avgAggregator.aggregate(event)
    }

    override fun getResult(): Long {
        val avg = avgAggregator.getResult()

        return round(avg.sum / (avg.count.toDouble()))
                .toLong()
    }

}
