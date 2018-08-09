package selenium_steps_support.service.webdriver_manager

import com.testerum.api.annotations.settings.annotation.DeclareSetting
import com.testerum.api.annotations.settings.annotation.DeclareSettings
import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.api.test_context.settings.model.SettingType
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.chrome.ChromeWebDriverFactory
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTINGS_CATEGORY
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_AFTER_STEP_DELAY_MILLIS
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import javax.annotation.concurrent.GuardedBy
import javax.annotation.concurrent.ThreadSafe

@ThreadSafe
@DeclareSettings([
    (DeclareSetting(
                key = SETTING_KEY_AFTER_STEP_DELAY_MILLIS,
                type = SettingType.NUMBER,
                defaultValue = "0",
                description = "Delay in milliseconds after a Selenium Step",
                category = SETTINGS_CATEGORY
        )),
    (DeclareSetting(
                key = SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST,
                type = SettingType.TEXT,
                defaultValue = SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT,
                description = """Leave browser open after a Selenium test. Possible values: "true", "false", "onFailure"""",
                category = SETTINGS_CATEGORY
        )),
    (DeclareSetting(
                key = SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP,
                type = SettingType.TEXT,
                defaultValue = "false",
                description = """Should take a screenshot after each Selenium Test. Possible values: "true", "false"""",
                category = SETTINGS_CATEGORY
        ))
])
class WebDriverManager(private val settingsManager: SettingsManager) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebDriverManager::class.java)

        internal const val SETTINGS_CATEGORY = "Selenium"

        internal const val SETTING_KEY_AFTER_STEP_DELAY_MILLIS = "testerum.selenium.afterStepDelayMillis"

        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST = "testerum.selenium.leaveBrowserOpenAfterTest"
        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT = "onFailure"

        internal const val SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP = "testerum.selenium.takeScreenshotAfterEachStep"
    }

    private val lock = Object()

    @GuardedBy("lock")
    private var _webDriver: WebDriver? = null

    private val currentWebDriver: WebDriver
        get() = synchronized(lock) {
            if (_webDriver == null) {
                _webDriver = ChromeWebDriverFactory.createWebDriver()
            }

            _webDriver!!
        }

    fun takeScreenshotToFile(): Path {
        val driver = currentWebDriver

        if (driver is TakesScreenshot) {
            val tempFileDeletedOnExit = driver.getScreenshotAs(OutputType.FILE).toPath()

            // todo: where should be put these files? in what directory? revisit this code when implementing reports
            val screenshotFile = Files.createTempFile("testerum-selenium-", tempFileDeletedOnExit.fileName.toString())

            return screenshotFile
        } else {
            throw IllegalArgumentException("current WebDriver does not support taking screenshots")
        }
    }

    fun executeWebDriverStep(block: (WebDriver) -> Unit) {
        block(currentWebDriver)

        // take screenshot
        if (settingsManager.getSettingValueOrDefault(SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP)!!.toBoolean()) {
            val screenshotFile = takeScreenshotToFile()
            LOGGER.info("step finished: screenshot saved at [${screenshotFile.toAbsolutePath()}]")
        }

        // sleep between steps
        TimeUnit.MILLISECONDS.sleep(
                settingsManager.getSettingValueOrDefault(SETTING_KEY_AFTER_STEP_DELAY_MILLIS)!!.toLong()
        )
    }

    fun waitUntil(block: (WebDriver) -> Boolean) {
        executeWebDriverStep { driver ->
            WebDriverWait(driver, 20)
                    .until(block)
        }

    }

    fun destroyCurrentWebDriverIfNeeded() {
        synchronized(lock) {
            if (_webDriver != null) {
                _webDriver!!.quit()
            }

            _webDriver = null
        }
    }
}
