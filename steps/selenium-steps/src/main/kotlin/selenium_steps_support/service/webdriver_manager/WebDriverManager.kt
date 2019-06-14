package selenium_steps_support.service.webdriver_manager

import com.testerum.api.annotations.settings.DeclareSetting
import com.testerum.api.annotations.settings.DeclareSettings
import com.testerum.api.test_context.settings.RunnerSettingsManager
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.common_jdk.OsUtils
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.webdriver_factory.chrome.ChromeWebDriverFactory
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTINGS_CATEGORY
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_AFTER_STEP_DELAY_MILLIS
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_WAIT_TIMEOUT_MILLIS
import java.nio.file.Files
import java.time.Duration
import java.util.concurrent.TimeUnit
import javax.annotation.concurrent.GuardedBy
import javax.annotation.concurrent.ThreadSafe
import java.nio.file.Path as JavaPath

@ThreadSafe
@DeclareSettings([
    DeclareSetting(
            key = WebDriverManager.SETTING_KEY_DRIVER,
            label = "Browser",
            type = SettingType.SELENIUM_DRIVER,
            defaultValue = """{"browserType": "CHROME", "browserExecutablePath": null, "headless": false, "driverVersion": "75.0.3770.8"}""",
            description = "The browser and selenium driver to use.",
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
    DeclareSetting(
            key = SETTING_KEY_AFTER_STEP_DELAY_MILLIS,
            label = "After step delay (millis)",
            type = SettingType.NUMBER,
            defaultValue = "0",
            description = "Delay in milliseconds after a Selenium Step",
            category = SETTINGS_CATEGORY
    ),
    DeclareSetting(
            key = SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST,
            label = "Leave browser open after test",
            type = SettingType.ENUM,
            defaultValue = SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT,
            enumValues = ["true", "false", "onFailure"],
            description = """Leave browser open after a Selenium test. Possible values: "true", "false", "onFailure"""",
            category = SETTINGS_CATEGORY
    ),
    DeclareSetting(
            key = SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP,
            label = "Take screenshot after each step",
            type = SettingType.BOOLEAN,
            defaultValue = "false",
            description = """Should take a screenshot after each Selenium Test. Possible values: "true", "false"""",
            category = SETTINGS_CATEGORY
    )
])
class WebDriverManager(private val runnerSettingsManager: RunnerSettingsManager) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(WebDriverManager::class.java)

        internal const val SETTINGS_CATEGORY = "Selenium"

        internal const val SETTING_KEY_WAIT_TIMEOUT_MILLIS = "testerum.selenium.waitTimeoutMillis"

        internal const val SETTING_KEY_AFTER_STEP_DELAY_MILLIS = "testerum.selenium.afterStepDelayMillis"

        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST = "testerum.selenium.leaveBrowserOpenAfterTest"
        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT = "onFailure"

        internal const val SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP = "testerum.selenium.takeScreenshotAfterEachStep"

        internal const val SETTING_KEY_DRIVER = "testerum.selenium.driver"
    }

    private val lock = Object()

    @GuardedBy("lock")
    private var _webDriver: WebDriver? = null

    private val currentWebDriver: WebDriver
        get() = synchronized(lock) {
            if (_webDriver == null) {
                _webDriver = ChromeWebDriverFactory.createWebDriver().apply {
                    // not maximizing on Mac because it makes WebDriver throw an exception for Chrome on Mac: "failed to change window state to normal, current state is maximized"
                    if (!OsUtils.IS_MAC) {
                        manage().window().maximize() // todo: make this configurable
                    }
                }
            }

            _webDriver!!
        }

    fun takeScreenshotToFile(): JavaPath {
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
        if (runnerSettingsManager.getRequiredSetting(SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP).resolvedValue.toBoolean()) {
            val screenshotFile = takeScreenshotToFile()
            LOG.info("step finished: screenshot saved at [${screenshotFile.toAbsolutePath()}]")
        }

        // sleep between steps
        TimeUnit.MILLISECONDS.sleep(
                runnerSettingsManager.getRequiredSetting(SETTING_KEY_AFTER_STEP_DELAY_MILLIS).resolvedValue.toLong()
        )
    }

    fun waitUntil(block: (WebDriver) -> Boolean) {
        val waitTimeoutMillis = runnerSettingsManager.getRequiredSetting(SETTING_KEY_WAIT_TIMEOUT_MILLIS).resolvedValue.toLong()

        executeWebDriverStep { driver ->
            WebDriverWait(driver, 0)
                    .withTimeout(Duration.ofMillis(waitTimeoutMillis))
                    .until(block)
        }
    }

    fun waitForElementPresent(elementLocator: String) {
        waitUntil { driver ->
            ElementLocatorService.locateElement(driver, elementLocator) != null
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
