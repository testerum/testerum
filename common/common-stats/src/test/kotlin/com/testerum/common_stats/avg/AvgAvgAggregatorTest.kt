package com.testerum.common_stats.avg

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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

        MatcherAssert.assertThat(avg.sum, CoreMatchers.`is`(CoreMatchers.equalTo(35L)))
        MatcherAssert.assertThat(avg.count, CoreMatchers.`is`(CoreMatchers.equalTo(5L)))
    }

}
