package selenium.actions.hooks

import com.testerum.api.annotations.hooks.AfterEachTest
import com.testerum.api.annotations.hooks.BeforeAllTests
import com.testerum.api.services.TesterumServiceLocator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_manager.WebDriverManager
import selenium_steps_support.service.webdriver_manager.WebDriverManager.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import selenium_steps_support.service.webdriver_manager.WebDriverManager.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT
import java.nio.file.Path

class WebDriverShutdownHook {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(WebDriverShutdownHook::class.java)
    }

    @BeforeAllTests
    fun registerShutdownHook() {
        // register a JVM shutdown hook, to prevent drivers & browsers from remaining open,
        // when the runner is stopped with e.g. a SIGTERM
        Runtime.getRuntime().addShutdownHook(Thread {
            destroyWebDriver()
        })
    }


    @AfterEachTest(order = Int.MAX_VALUE /*to make this hook runs last*/)
    fun destroyWebDriver() {
        takeScreenshotIfFailed()
        closeBrowserIfNeeded()
    }

    private fun takeScreenshotIfFailed() {
        if (TesterumServiceLocator.getTestContext().testStatus.isFailedOrError()) {
            val screenshotFile: Path = WebDriverManager.takeScreenshotToFile()
            LOGGER.info("failed test: screenshot saved at [${screenshotFile.toAbsolutePath()}]")
        }
    }

    private fun closeBrowserIfNeeded() {
        val leaveBrowserOpenAfterTest: String = TesterumServiceLocator.getSettingsManager().getSettingValueOrDefault(SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST)
                ?: SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST_DEFAULT

        val leaveBrowserOpen: Boolean = when (leaveBrowserOpenAfterTest) {
            "true"      -> true
            "false"     -> false
            "onFailure" -> TesterumServiceLocator.getTestContext().testStatus.isFailedOrError()
            else        -> {
                LOGGER.error(
                        "error reading property [$SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST]" +
                        ": the value [$leaveBrowserOpenAfterTest] is not valid; valid values are [true], [false], or [onFailure]"
                )

                true
            }
        }

        if (!leaveBrowserOpen) {
            WebDriverManager.destroyCurrentWebDriverIfNeeded()
        }
    }
}