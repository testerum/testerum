package com.testerum.common_stats

import java.util.*

class GroupingAggregator<E, K, R>(private val extractKey: (E) -> K,
                                  private val createValueAggregator: () -> Aggregator<E, R>) : Aggregator<E, Map<K, R>> {

    private val aggregators = TreeMap<K, Aggregator<E, R>>()

    override fun aggregate(event: E) {
        val key = extractKey(event)

        val aggregator = getOrCreateAggregator(key)

        aggregator.aggregate(event)
    }

    private fun getOrCreateAggregator(key: K): Aggregator<E, R> {
        return aggregators.computeIfAbsent(key) { createValueAggregator() }
    }

    override fun getResult(): Map<K, R> {
        return aggregators.mapValues { entry -> entry.value.getResult() }
    }

}
