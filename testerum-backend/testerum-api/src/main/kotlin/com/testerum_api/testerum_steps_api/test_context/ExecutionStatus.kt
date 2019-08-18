package com.testerum_api.testerum_steps_api.test_context

enum class ExecutionStatus {

    // VERY IMPORTANT
    // --------------
    //
    // Don't change the order of the constants in this enum.
    // The constants are listed in increasing order of importance.
    //
    // For example, when you have 2 children in the runner tree, one FAILED and another UNDEFINED, the status
    // of the parent node will be FAILED, because FAILED is more important than UNDEFINED.
    //

    DISABLED,
    PASSED,
    SKIPPED,
    UNDEFINED,
    FAILED,
    ;

}
