package com.testerum.common_stats.avg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RoundingToLongAvgAggregatorTest {

    @Test
    fun `integer average`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val avg = aggregator.getResult()

        assertThat(avg).isEqualTo(15L)
    }

    @Test
    fun `round down`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(1)
        aggregator.aggregate(2)
        aggregator.aggregate(7)

        val avg = aggregator.getResult()

        assertThat(avg).isEqualTo(3L)
    }

    @Test
    fun `round up`() {
        val aggregator = RoundingToLongAvgAggregator()

        aggregator.aggregate(1)
        aggregator.aggregate(2)
        aggregator.aggregate(8)

        val avg = aggregator.getResult()

        assertThat(avg).isEqualTo(4L)
    }

}
