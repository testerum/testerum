package net.qutester.model.warning

data class Warning(val type: WarningType,
                   val message: String) {

    companion object {
        val UNDEFINED_STEP_CALL = Warning(
                type = WarningType.UNDEFINED_STEP_CALL,
                message = "This step is not defined."
        )

        val TEST_WITHOUT_STEP_CALLS = Warning(
                type = WarningType.NO_STEP_CALLS,
                message = "This test doesn't call any steps."
        )
        val COMPOSED_STEP_WITHOUT_STEP_CALLS = Warning(
                type = WarningType.NO_STEP_CALLS,
                message = "This composed step doesn't call any steps."
        )
    }

}
