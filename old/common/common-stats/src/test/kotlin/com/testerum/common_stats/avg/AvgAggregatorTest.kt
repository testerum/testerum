package com.testerum.common_stats.avg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AvgAggregatorTest {

    @Test
    fun test() {
        val aggregator = AvgAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val avg = aggregator.getResult()

        assertThat(avg.sum).isEqualTo(45L)
        assertThat(avg.count).isEqualTo(3L)
    }

}
