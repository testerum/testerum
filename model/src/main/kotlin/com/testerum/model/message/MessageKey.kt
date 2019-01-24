package com.testerum.model.message

enum class MessageKey(val defaultValue: String) {

    WARNING_TEST_WITHOUT_STEP_CALLS("This test doesn't call any steps."),
    WARNING_UNDEFINED_STEP_CALL("This step is not defined."),
    WARNING_COMPOSED_STEP_WITHOUT_STEP_CALLS("This composed step doesn't call any steps."),
    WARNING_ARG_EXTERNAL_RESOURCE_NOT_FOUND("This parameter refers to a missing external resource: [{0}]."),
    WARNING_ARG_EXTERNAL_RESOURCE_OF_UNKNOWN_TYPE("This parameter refers to an external resource of unknown type: [{0}]."),

}