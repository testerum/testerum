package com.testerum.common_stats

import com.testerum.common_stats.sum.LongSumAggregator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        assertThat(result).hasSize(5)

        assertThat(result["Assembler"]).isEqualTo(2L)
        assertThat(result["C"]).isEqualTo(250L)
        assertThat(result["Pascal"]).isEqualTo(50L)
        assertThat(result["Java"]).isEqualTo(3_500L)
        assertThat(result["Kotlin"]).isEqualTo(25_000L)
    }

}
