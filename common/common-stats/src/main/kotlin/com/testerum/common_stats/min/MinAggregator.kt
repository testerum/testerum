package com.testerum.common_stats.min

import com.testerum.common_stats.Aggregator
import kotlin.math.min

class MinAggregator : Aggregator<Long, Long> {

    private var result: Long = 0

    override fun aggregate(event: Long) {
        result = min(result, event)
    }


    override fun getResult(): Long = result

}
