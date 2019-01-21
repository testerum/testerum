package com.testerum.common_stats

import com.testerum.common_stats.count.CountAggregator
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.aMapWithSize
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class GroupingAggregatorTest {

    @Test
    fun test() {
        // - groups names by their first letter (initial)
        // - the aggregated value (for each letter) is the number of names with that initial
        val aggregator = GroupingAggregator<String, Char, Long>(
                extractKey = { name -> name [0] },
                createValueAggregator = { CountAggregator() }
        )

        aggregator.aggregate("Cristian")
        aggregator.aggregate("Corina")

        aggregator.aggregate("Ionut")
        aggregator.aggregate("Immanuel")
        aggregator.aggregate("Ishtar")

        val namesCountByInitial = aggregator.getResult()

        assertThat(namesCountByInitial, Is(aMapWithSize(2)))
        assertThat(namesCountByInitial['C'], Is(equalTo(2L)))
        assertThat(namesCountByInitial['I'], Is(equalTo(3L)))
    }

}
