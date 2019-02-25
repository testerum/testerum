package com.testerum.common_stats.avg

import com.testerum.common_stats.Aggregator

class AvgAggregator : Aggregator<Long, Avg> {

    private var sum: Long = 0
    private var count: Long = 0

    override fun aggregate(event: Long) {
        sum += event
        count++
    }

    override fun getResult(): Avg {
        return Avg(
                sum = sum,
                count = count
        )
    }

}
