package selenium.actions.hooks

import com.testerum_api.testerum_steps_api.annotations.hooks.AfterEachTest
import com.testerum_api.testerum_steps_api.annotations.hooks.BeforeAllTests
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.ExecutionStatus
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager
import selenium_steps_support.service.webdriver_manager.WebDriverManager.Companion.SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST
import java.nio.file.Path as JavaPath

class WebDriverShutdownHook {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(WebDriverShutdownHook::class.java)
    }

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @com.testerum_api.testerum_steps_api.annotations.hooks.BeforeAllTests
    fun registerShutdownHook() {
        // register a JVM shutdown hook, to prevent drivers & browsers from remaining open,
        // when the runner is stopped with e.g. a SIGTERM
        Runtime.getRuntime().addShutdownHook(Thread {
            destroyWebDriver()
        })
    }


    @com.testerum_api.testerum_steps_api.annotations.hooks.AfterEachTest(order = Int.MAX_VALUE /*to make this hook runs last*/)
    fun destroyWebDriver() {
        takeScreenshotIfFailed()
        closeBrowserIfNeeded()
    }

    private fun takeScreenshotIfFailed() {
        if (TesterumServiceLocator.getTestContext().testStatus == ExecutionStatus.FAILED) {
            val screenshotFile: JavaPath = webDriverManager.takeScreenshotToFile()
            LOG.info("failed test: screenshot saved at [${screenshotFile.toAbsolutePath()}]")
        }
    }

    private fun closeBrowserIfNeeded() {
        val leaveBrowserOpenAfterTest: String = TesterumServiceLocator.getSettingsManager().getRequiredSetting(SETTING_KEY_LEAVE_BROWSER_OPEN_AFTER_TEST).resolvedValue

        val leaveBrowserOpen: Boolean = when (leaveBrowserOpenAfterTest) {
            "true"      -> true
            "false"     -> false
            "onFailure" -> TesterumServiceLocator.getTestContext().testStatus == ExecutionStatus.FAILED
            else        -> {
                LOG.error(
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
