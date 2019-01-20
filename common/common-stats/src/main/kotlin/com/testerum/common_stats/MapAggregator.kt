package com.testerum.common_stats

import java.util.*

/**
 * Aggregates maps, by aggregating values; keys are not transformed in any way.
 */
class MapAggregator<K, V>(private val createValueAggregator: () -> Aggregator<V, V>) : Aggregator<Map<K, V>, Map<K, V>> {

    private val aggregators = TreeMap<K, Aggregator<V, V>>()

    override fun aggregate(event: Map<K, V>) {
        for ((key, value) in event) {
            val aggregator = getOrCreateAggregator(key)

            aggregator.aggregate(value)
        }
    }

    private fun getOrCreateAggregator(key: K): Aggregator<V, V> {
        return aggregators.computeIfAbsent(key) { createValueAggregator() }
    }

    override fun getResult(): Map<K, V> {
        return aggregators.mapValues { entry -> entry.value.getResult() }
    }

}
