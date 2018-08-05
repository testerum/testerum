package selenium.actions.hooks

import com.testerum.api.annotations.hooks.AfterEachTest
import com.testerum.api.test_context.TestContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import selenium_steps_support.service.webdriver_manager.WebDriverManager
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT
import java.nio.file.Path

class WebDriverShutdownHook @Autowired constructor(private val webDriverManager: WebDriverManager,
                                                   private val testContext: TestContext) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebDriverShutdownHook::class.java)
    }

    @AfterEachTest(order = Int.MAX_VALUE /*to make this hook runs last*/)
    fun destroyWebDriver() {
        takeScreenshotIfFailed()
        closeBrowserIfNeeded()
    }

    private fun takeScreenshotIfFailed() {
        if (testContext.testStatus.isFailedOrError()) {
            val screenshotFile: Path = webDriverManager.takeScreenshotToFile()
            LOGGER.info("failed test: screenshot saved at [${screenshotFile.toAbsolutePath()}]")
        }
    }

    private fun closeBrowserIfNeeded() {
        val leaveBrowserOpenAfterTest: String = testContext.settingsManager.getSettingValueOrDefault(SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST)
                ?: SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT

        val leaveBrowserOpen: Boolean = when (leaveBrowserOpenAfterTest) {
            "true"      -> true
            "false"     -> false
            "onFailure" -> testContext.testStatus.isFailedOrError()
            else        -> {
                LOGGER.error(
                        "error reading property [$SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST]" +
                        ": the value [$leaveBrowserOpenAfterTest] is not valid; valid values are [true], [false], or [onFailure]"
                )

                true
            }
        }

        if (!leaveBrowserOpen) {
            webDriverManager.destroyCurrentWebDriverIfNeeded()
        }
    }
}