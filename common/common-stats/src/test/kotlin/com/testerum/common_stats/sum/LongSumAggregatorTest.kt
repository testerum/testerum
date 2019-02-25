package com.testerum.common_stats.sum

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class LongSumAggregatorTest {

    @Test
    fun test() {
        val aggregator = LongSumAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val sum = aggregator.getResult()

        assertThat(sum, Is(equalTo(45L)))
    }

}
