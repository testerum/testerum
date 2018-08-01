package net.qutester.model.warning

import net.qutester.model.message.MessageKey
import java.text.MessageFormat

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

        fun externalResourceNotFound(resourcePath: String): Warning {
            val messageTemplate = MessageKey.WARNING_ARG_EXTERNAL_RESOURCE_NOT_FOUND.defaultValue

            val message = MessageFormat.format(messageTemplate, resourcePath)

            return Warning(
                    type = WarningType.EXTERNAL_RESOURCE_NOT_FOUND,
                    message = message
            )
        }

        fun externalResourceOfUnknownType(resourcePath: String): Warning {
            val messageTemplate = MessageKey.WARNING_ARG_EXTERNAL_RESOURCE_OF_UNKNOWN_TYPE.defaultValue

            val message = MessageFormat.format(messageTemplate, resourcePath)

            return Warning(
                    type = WarningType.EXTERNAL_RESOURCE_OF_UNKNOWN_TYPE,
                    message = message
            )
        }
    }

}
