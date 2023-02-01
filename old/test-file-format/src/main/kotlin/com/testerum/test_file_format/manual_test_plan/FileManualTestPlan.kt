package com.testerum.test_file_format.manual_test_plan

import java.time.LocalDateTime

data class FileManualTestPlan(val description: String? = null,
                              val createdDateUtc: LocalDateTime? = null,
                              val isFinalized: Boolean = FileManualTestPlan.IS_FINALIZED_DEFAULT,
                              val finalizedDateUtc: LocalDateTime? = null) {

    companion object {
        val IS_FINALIZED_DEFAULT = false
    }

    init {
        if (!isFinalized && finalizedDateUtc != null) {
            throw IllegalArgumentException("cannot have a finalization date if the plan is not finalized")
        }
    }

}
