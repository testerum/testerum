package com.testerum.model.warning

enum class WarningType {

    NO_STEP_CALLS,       // empty test or composed step
    UNDEFINED_STEP_CALL,
    EXTERNAL_RESOURCE_NOT_FOUND,
    EXTERNAL_RESOURCE_OF_UNKNOWN_TYPE,
    ;

}
