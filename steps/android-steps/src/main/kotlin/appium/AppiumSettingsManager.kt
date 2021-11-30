package appium

import appium.AppiumSettingsManager.Companion.SETTINGS_CATEGORY
import appium.AppiumSettingsManager.Companion.SETTING_KEY_WAIT_TIMEOUT_MILLIS
import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSetting
import com.testerum_api.testerum_steps_api.annotations.settings.DeclareSettings
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.settings.RunnerSettingsManager
import com.testerum_api.testerum_steps_api.test_context.settings.model.SettingType

@DeclareSettings([

    DeclareSetting(
        key = AppiumSettingsManager.SETTING_KEY_ANDROID_APPLICATION_PATH,
        label = "APK or APKS path",
        type = SettingType.FILESYSTEM_DIRECTORY,
        defaultValue = "",
        description = "Path to the app being tested.\n" +
            "Appium for Android supports application .apk and .apks bundles.\n" +
            "If this capability is not set then your test starts on Dashboard view.\n" +
            "It is also possible to provide an URL where the app is located.",
        category = SETTINGS_CATEGORY
    ),
    DeclareSetting(
        key = SETTING_KEY_WAIT_TIMEOUT_MILLIS,
        label = "Wait timeout (millis)",
        type = SettingType.NUMBER,
        defaultValue = "5000",
        description = "Maximum time duration in milliseconds for wait steps (e.g. wait until an element is present on the page).",
        category = SETTINGS_CATEGORY
    ),
])
class AppiumSettingsManager {

    companion object {
        internal const val SETTINGS_CATEGORY = "Appium"
        internal const val SETTING_KEY_ANDROID_APPLICATION_PATH = "testerum.android.application.path"
        internal const val SETTING_KEY_WAIT_TIMEOUT_MILLIS = "testerum.android.waitTimeoutMillis"
    }

    private val runnerSettingsManager: RunnerSettingsManager = TesterumServiceLocator.getSettingsManager();

    fun applicationPath() =  runnerSettingsManager.getSetting(SETTING_KEY_ANDROID_APPLICATION_PATH)?.resolvedValue
    fun waitTimeoutMillis() =  runnerSettingsManager.getRequiredSetting(SETTING_KEY_WAIT_TIMEOUT_MILLIS).resolvedValue.toLong()

}
