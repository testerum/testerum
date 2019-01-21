package com.testerum.common_stats.avg

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class RoundingToLongAvgAggregatorTest {

    @Test
    fun `integer average`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val avg = aggregator.getResult()

        assertThat(avg, Is(equalTo(15L)))
    }

    @Test
    fun `round down`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(1)
        aggregator.aggregate(2)
        aggregator.aggregate(7)

        val avg = aggregator.getResult()

        assertThat(avg, Is(equalTo(3L)))
    }

    @Test
    fun `round up`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(1)
        aggregator.aggregate(2)
        aggregator.aggregate(8)

        val avg = aggregator.getResult()

        assertThat(avg, Is(equalTo(4L)))
    }

}
