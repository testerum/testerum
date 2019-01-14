package com.testerum.common_stats.count

import com.testerum.common_stats.Aggregator

class CountAggregator : Aggregator<Any, Long> {

    private var result: Long = 0

    override fun aggregate(event: Any) {
        result++
    }


    override fun getResult(): Long = result

}
