package com.testerum.report_generators.reports.report_model.base.mapper.utils

import java.util.concurrent.atomic.AtomicLong

/**
 * Generates unique IDs, minimizing the number of characters.
 */
class SpaceMinimizingIdGenerator {
    private val lastId = AtomicLong(0)

    fun nextId(): String {
        val id = lastId.incrementAndGet()

        return id.toString(Character.MAX_RADIX)
    }
}
