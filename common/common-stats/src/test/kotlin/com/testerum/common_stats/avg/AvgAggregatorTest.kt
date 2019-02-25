package com.testerum.common_stats.avg

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class AvgAggregatorTest {

    @Test
    fun test() {
        val aggregator = AvgAggregator()

        aggregator.aggregate(10)
        aggregator.aggregate(15)
        aggregator.aggregate(20)

        val avg = aggregator.getResult()

        assertThat(avg.sum, Is(equalTo(45L)))
        assertThat(avg.count, Is(equalTo(3L)))
    }

}
