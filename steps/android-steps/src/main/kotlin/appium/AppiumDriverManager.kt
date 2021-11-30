package appium

import appium.hooks.AppiumService
import appium.model.AppiumDriverType
import appium.model.AppiumDriverType.*
import appium.model.AppiumSettings
import com.testerum_api.testerum_steps_api.annotations.hooks.AfterAllTests
import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.AndroidElement
import org.openqa.selenium.remote.DesiredCapabilities
import javax.annotation.concurrent.GuardedBy
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
class AppiumDriverManager() {

    private val lock = Object()

    @GuardedBy("lock")
    private var _appiumDrivers: MutableMap<AppiumDriverType, AppiumDriver<*>> = mutableMapOf()

    fun driver(driverType: AppiumDriverType): AppiumDriver<*> {
        return synchronized(lock) {
            val driver = _appiumDrivers[driverType]

            if (driver != null) return@synchronized driver

            val appiumSettings = getDriverSettings()

            return when(driverType) {
                ANDROID -> {
                    AppiumService.globalSetup()

                    val capabilities = DesiredCapabilities()
                    capabilities.setCapability("deviceName", appiumSettings.androidDeviceName)
                    capabilities.setCapability("app", appiumSettings.androidApplicationPath)
                    capabilities.setCapability("appPackage", appiumSettings.androidApplicationPackage)
                    capabilities.setCapability("appActivity", appiumSettings.androidApplicationActivity)

                    val androidDriver = AndroidDriver<AndroidElement>(AppiumService.getServiceUrl(), capabilities)
                    _appiumDrivers[ANDROID] = androidDriver

                    androidDriver
                }
                IOS, WINDOWS, MAC, CHROME, EDGE, FIREFOX, INTERNET_EXPLORER, OPERA, REMOTE, SAFARI -> {
                    throw NotImplementedError("Not implemented capability")
                }
            }
        }
    }

    @AfterAllTests
    fun destroyDrivers() {
        _appiumDrivers.values.forEach {
            it.quit()
        }
        _appiumDrivers.clear()
    }

    private fun getDriverSettings(): AppiumSettings {

        return AppiumSettings(
            androidDeviceName = "Android Emulator",
            androidApplicationPath = AppiumServiceLocator.settingsManager.applicationPath(),
            androidApplicationPackage = "io.appium.android.apis",
            androidApplicationActivity = "ApiDemos"
        )
    }
}
