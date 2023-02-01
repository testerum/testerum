package http_support

import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSetting
import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSettings
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType
import http_support.HttpStepsSettingsManager.Companion.SETTINGS_CATEGORY
import http_support.HttpStepsSettingsManager.Companion.SETTINGS_KEY_CONNECTION_TIMEOUT_MILLIS
import http_support.HttpStepsSettingsManager.Companion.SETTINGS_KEY_SOCKET_TIMEOUT_MILLIS

@DeclareSettings([
    DeclareSetting(
            key = SETTINGS_KEY_CONNECTION_TIMEOUT_MILLIS,
            label = "Connection timeout (millis)",
            type = SettingType.NUMBER,
            defaultValue = "0",
            description = "The timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an infinite timeout.",
            category = SETTINGS_CATEGORY
    ),
    DeclareSetting(
            key = SETTINGS_KEY_SOCKET_TIMEOUT_MILLIS,
            label = "Socket timeout (millis)",
            type = SettingType.NUMBER,
            defaultValue = "0",
            description = "The timeout in milliseconds for waiting for data, or, put differently the maximum period of inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.",
            category = SETTINGS_CATEGORY
    )
])
class HttpStepsSettingsManager(private val runnerSettingsManager: RunnerSettingsManager) {

    companion object {
        internal const val SETTINGS_CATEGORY = "HTTP"

        internal const val SETTINGS_KEY_CONNECTION_TIMEOUT_MILLIS = "testerum.http.connectionTimeoutMillis"
        internal const val SETTINGS_KEY_SOCKET_TIMEOUT_MILLIS     = "testerum.http.socketTimeoutMillis"
    }

    fun getConnectionTimeoutMillis(): Int = runnerSettingsManager.getRequiredSetting(SETTINGS_KEY_CONNECTION_TIMEOUT_MILLIS).resolvedValue.toInt()

    fun getSocketTimeoutMillis(): Int = runnerSettingsManager.getRequiredSetting(SETTINGS_KEY_SOCKET_TIMEOUT_MILLIS).resolvedValue.toInt()

}
