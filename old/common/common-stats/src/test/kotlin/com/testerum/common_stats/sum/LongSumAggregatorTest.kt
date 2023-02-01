package com.testerum.common_stats.sum

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class LongSumAggregatorTest {

    @Test
    fun test() {
        val aggregator = LongSumAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val sum = aggregator.getResult()

        assertThat(sum).isEqualTo(45L)
    }

}
