package com.testerum.common_stats.max

import com.testerum.common_stats.Aggregator
import kotlin.math.max

class MaxAggregator : Aggregator<Long, Long> {

    private var result: Long = 0

    override fun aggregate(event: Long) {
        result = max(result, event)
    }


    override fun getResult(): Long = result

}
