package com.testerum.runner.statistics_model

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus

class StatsCountByStatus constructor(countByStatusMap: Map<ExecutionStatus, Long>) {

    @get:JsonAnyGetter
    val countByStatusMap = countByStatusMap.filter {
        // zero-count is implicit: if there is no entry in the map for a status, the count it zero
        it.value != 0L
    }

    fun getCount(status: ExecutionStatus): Long = countByStatusMap[status] ?: 0

    override fun toString(): String = "StatsCountByStatus $countByStatusMap"

}
