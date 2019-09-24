package selenium_steps_support.service.webdriver_manager

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.api.annotations.settings.DeclareSetting
import com.testerum.api.annotations.settings.DeclareSettings
import com.testerum.api.test_context.settings.RunnerSettingsManager
import com.testerum.api.test_context.settings.model.SeleniumBrowserType
import com.testerum.api.test_context.settings.model.SeleniumDriverSettingValue
import com.testerum.api.test_context.settings.model.SettingType
import com.testerum.common_jdk.OsUtils
import com.testerum.file_service.file.SeleniumDriversFileService
import org.openqa.selenium.OutputType
import org.openqa.selenium.TakesScreenshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.WebDriverWait
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.webdriver_factory.WebDriverFactories
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTINGS_CATEGORY
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_AFTER_STEP_DELAY_MILLIS
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_WAIT_TIMEOUT_MILLIS
import selenium_steps_support.utils.SeleniumStepsDirs
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
            defaultValue = """{"browserType": "CHROME", "browserExecutablePath": null, "headless": false, "driverVersion": null, "remoteUrl": null}""",
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
            description = """Takes a screenshot after each Selenium Test. Possible values: "true", "false"""",
            category = SETTINGS_CATEGORY
    )
])
class WebDriverManager(private val runnerSettingsManager: RunnerSettingsManager,
                       private val seleniumDriversFileService: SeleniumDriversFileService) {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(WebDriverManager::class.java)

        internal const val SETTINGS_CATEGORY = "Selenium"

        internal const val SETTING_KEY_WAIT_TIMEOUT_MILLIS = "testerum.selenium.waitTimeoutMillis"

        internal const val SETTING_KEY_AFTER_STEP_DELAY_MILLIS = "testerum.selenium.afterStepDelayMillis"

        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST = "testerum.selenium.leaveBrowserOpenAfterTest"
        internal const val SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT = "onFailure"

        internal const val SETTING_KEY_TAKE_SCREENSHOT_AFTER_EACH_STEP = "testerum.selenium.takeScreenshotAfterEachStep"

        internal const val SETTING_KEY_DRIVER = "testerum.selenium.driver"
        internal const val SETTING_KEY_DRIVER_BROWSER_TYPE = "testerum.selenium.driver.browserType"
        internal const val SETTING_KEY_DRIVER_BROWSER_EXECUTABLE_PATH = "testerum.selenium.driver.browserExecutablePath"
        internal const val SETTING_KEY_DRIVER_HEADLESS = "testerum.selenium.driver.headless"
        internal const val SETTING_KEY_DRIVER_DRIVER_VERSION = "testerum.selenium.driver.driverVersion"
        internal const val SETTING_KEY_DRIVER_REMOTE_URL = "testerum.selenium.driver.remoteUrl"

        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    private val lock = Object()

    @GuardedBy("lock")
    private var _webDriver: WebDriver? = null

    private val tempDirScreenshotsDirectory: JavaPath by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Files.createTempDirectory("testerum-screenshots")
    }

    private val currentWebDriver: WebDriver
        get() = synchronized(lock) {
            if (_webDriver == null) {
                val seleniumDriverSetting = getSeleniumDriverSetting()
                val webDriverFactory = WebDriverFactories.getWebDriverFactory(seleniumDriverSetting.browserType)
                val seleniumDriversByBrowser = seleniumDriversFileService.getDriversInfo(SeleniumStepsDirs.getSeleniumDriversDir())

                val webDriver = webDriverFactory.createWebDriver(seleniumDriverSetting, seleniumDriversByBrowser)

                _webDriver = webDriver.apply {
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
            val screenshotFile = Files.createTempFile(tempDirScreenshotsDirectory, "testerum-selenium-", tempFileDeletedOnExit.fileName.toString())

            Files.copy(tempFileDeletedOnExit, screenshotFile)

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

    fun switchCurrentWebDriver(block: (current: WebDriver) -> WebDriver) {
        switchCurrentWebDriverToTop()
        _webDriver = block(currentWebDriver)
    }

    fun switchCurrentWebDriverToTop() {
        currentWebDriver.switchTo().defaultContent()
    }

    fun closeWindow() {
        currentWebDriver.close()
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

    private fun getSeleniumDriverSetting(): SeleniumDriverSettingValue {
        val unparsedDriver = runnerSettingsManager.getRequiredSetting(SETTING_KEY_DRIVER).resolvedValue

        var seleniumDriverSettingValue = OBJECT_MAPPER.readValue<SeleniumDriverSettingValue>(unparsedDriver)

        // check for overrides: browserType
        val browserType: SeleniumBrowserType? = runnerSettingsManager.getSetting(SETTING_KEY_DRIVER_BROWSER_TYPE)?.resolvedValue?.let {
            SeleniumBrowserType.safeValueOf(it)
                    ?: throw IllegalArgumentException(
                            "invalid value [$it] for setting [$SETTING_KEY_DRIVER_BROWSER_TYPE]" +
                                    ": it should be one of the following ${SeleniumBrowserType.values().toList()}"
                    )
        }
        if (browserType != null) {
            seleniumDriverSettingValue = seleniumDriverSettingValue.copy(browserType = browserType)
        }

        // check for overrides: browserExecutablePath
        val browserExecutablePath: String? = runnerSettingsManager.getSetting(SETTING_KEY_DRIVER_BROWSER_EXECUTABLE_PATH)?.resolvedValue
        if (browserExecutablePath != null) {
            seleniumDriverSettingValue = seleniumDriverSettingValue.copy(browserExecutablePath = browserExecutablePath)
        }

        // check for overrides: headless
        val headless: Boolean? = runnerSettingsManager.getSetting(SETTING_KEY_DRIVER_HEADLESS)?.resolvedValue?.let {
            it.toBoolean()
        }
        if (headless != null) {
            seleniumDriverSettingValue = seleniumDriverSettingValue.copy(headless = headless)
        }

        // check for overrides: driverVersion
        val driverVersion: String? = runnerSettingsManager.getSetting(SETTING_KEY_DRIVER_DRIVER_VERSION)?.resolvedValue
        if (driverVersion != null) {
            seleniumDriverSettingValue = seleniumDriverSettingValue.copy(driverVersion = driverVersion)
        }

        // check for overrides: remoteUrl
        val remoteUrl: String? = runnerSettingsManager.getSetting(SETTING_KEY_DRIVER_REMOTE_URL)?.resolvedValue
        if (remoteUrl != null) {
            seleniumDriverSettingValue = seleniumDriverSettingValue.copy(remoteUrl = remoteUrl)
        }

        return seleniumDriverSettingValue
    }

}
