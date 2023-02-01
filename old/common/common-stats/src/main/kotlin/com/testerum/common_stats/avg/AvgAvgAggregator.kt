package com.testerum.common_stats.avg

import com.testerum.common_stats.Aggregator

class AvgAvgAggregator : Aggregator<Avg, Avg> {

    private var sum: Long = 0
    private var count: Long = 0

    override fun aggregate(event: Avg) {
        sum   += event.sum
        count += event.count
    }

    override fun getResult(): Avg {
        return Avg(
                sum = sum,
                count = count
        )
    }

}
