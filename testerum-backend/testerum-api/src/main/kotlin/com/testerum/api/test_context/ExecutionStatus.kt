package com.testerum.api.test_context

enum class ExecutionStatus {

    PASSED,
    FAILED,
    DISABLED,
    ERROR,
    UNDEFINED,
    SKIPPED,
    ;

    fun isFailedOrError(): Boolean
            = (this == FAILED) || (this == ERROR)

}
