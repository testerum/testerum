package com.testerum.common_stats.sum

import com.testerum.common_stats.Aggregator

class LongSumAggregator : Aggregator<Long, Long> {

    private var result: Long = 0

    override fun aggregate(event: Long) {
        result += event
    }

    override fun getResult(): Long = result

}
