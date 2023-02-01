package com.testerum.common_stats.avg

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AvgAvgAggregatorTest {

    @Test
    fun test() {
        val aggregator = AvgAvgAggregator()

        aggregator.aggregate(
            Avg(sum = 0, count = 1)
        )
        aggregator.aggregate(
            Avg(sum = 15, count = 3)
        )
        aggregator.aggregate(
            Avg(sum = 20, count = 1)
        )

        val avg = aggregator.getResult()

        assertThat(avg.sum).isEqualTo(35L)
        assertThat(avg.count).isEqualTo(5L)
    }

}
