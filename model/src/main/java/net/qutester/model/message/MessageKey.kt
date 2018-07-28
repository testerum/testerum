package net.qutester.model.message

enum class MessageKey(val defaultValue: String) {

    WARNING_TEST_WITHOUT_STEP_CALLS("This test doesn't call any steps."),
    WARNING_UNDEFINED_STEP_CALL("This step is not defined."),
    WARNING_COMPOSED_STEP_WITHOUT_STEP_CALLS("This composed step doesn't call any steps.")


}