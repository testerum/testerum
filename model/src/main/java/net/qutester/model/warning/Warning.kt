package net.qutester.model.warning

import net.qutester.model.message.MessageKey

data class Warning(val type: WarningType,
                   val message: String) {

    companion object {
        val UNDEFINED_STEP_CALL = Warning(
                type = WarningType.UNDEFINED_STEP_CALL,
                message = MessageKey.WARNING_UNDEFINED_STEP_CALL.defaultValue
        )

        val TEST_WITHOUT_STEP_CALLS = Warning(
                type = WarningType.NO_STEP_CALLS,
                message = MessageKey.WARNING_TEST_WITHOUT_STEP_CALLS.defaultValue
        )

        val COMPOSED_STEP_WITHOUT_STEP_CALLS = Warning(
                type = WarningType.NO_STEP_CALLS,
                message = MessageKey.WARNING_COMPOSED_STEP_WITHOUT_STEP_CALLS.defaultValue
        )
    }

}
