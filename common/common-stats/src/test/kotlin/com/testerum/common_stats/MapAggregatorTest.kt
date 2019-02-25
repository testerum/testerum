package com.testerum.common_stats

import com.testerum.common_stats.sum.LongSumAggregator
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class MapAggregatorTest {
    @Test
    fun test() {
        val aggregator = MapAggregator</*programmingLanguage: */String, /*votes: */Long>(
                createValueAggregator = { LongSumAggregator() }
        )

        aggregator.aggregate(
                mapOf(
                        "Assembler" to 2L,
                        "C" to 100L,
                        "Java" to 1000L
                )
        )
        aggregator.aggregate(
                mapOf(
                        "Pascal" to 50L,
                        "C" to 150L,
                        "Java" to 1600L,
                        "Kotlin" to 10_000L
                )
        )
        aggregator.aggregate(
                mapOf(
                        "Java" to 900L,
                        "Kotlin" to 15_000L
                )
        )

        val result = aggregator.getResult()
        assertThat(result, Is(Matchers.aMapWithSize(5)))

        assertThat(result["Assembler"], Is(equalTo(2L)))
        assertThat(result["C"], Is(equalTo(250L)))
        assertThat(result["Pascal"], Is(equalTo(50L)))
        assertThat(result["Java"], Is(equalTo(3_500L)))
        assertThat(result["Kotlin"], Is(equalTo(25_000L)))
    }

}
