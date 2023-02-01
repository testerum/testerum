package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.remote.CapabilityType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.WebDriverFactory
import selenium_steps_support.utils.SeleniumStepsDirs
import java.nio.file.Path as JavaPath

object FirefoxWebDriverFactory : WebDriverFactory {

    private val LOG: Logger = LoggerFactory.getLogger(FirefoxWebDriverFactory::class.java)

    override fun createWebDriver(
        config: SeleniumDriverSettingValue,
        webDriverCustomizationScript: String?,
        driversByBrowser: SeleniumDriversByBrowser
    ): WebDriver {
        val driverInfo = driversByBrowser.getDriverInfoByBrowserAndDriverVersion(
            browserType = config.browserType,
            driverVersion = config.driverVersion
        ) ?: throw RuntimeException("could not find a Selenium driver for browserType=[${config.browserType}] and driverVersion=[${config.driverVersion}]")

        val relativePath = driverInfo.relativePath
        if (relativePath == null) {
            LOG.info("using system Selenium driver")
        } else {
            val driverBinaryPath: JavaPath = SeleniumStepsDirs.getSeleniumDriversDir()
                .resolve(relativePath)
                .toAbsolutePath()
                .normalize()

            LOG.info("using Selenium driver [$driverBinaryPath]")

            // todo: this is nasty: it's a global variable preventing us from using different drivers (e.g. different Firefox versions) at the same time
            // ==> find a better way
            System.setProperty("webdriver.gecko.driver", driverBinaryPath.toAbsolutePath().toString())
        }

        val options = FirefoxOptions()
        options.setHeadless(config.headless)
        if (config.browserExecutablePath != null) {
            LOG.info("using Firefox executable [${config.browserExecutablePath}]")
            options.setBinary(config.browserExecutablePath)
        } else {
            LOG.info("using system Firefox installation")
        }

        options.addArguments("--width=1920", "--height=1080")
        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)

        if (!webDriverCustomizationScript.isNullOrBlank()) {
            ExpressionEvaluator.evaluate(
                expression = webDriverCustomizationScript,
                context = mapOf(
                    "capabilities" to options
                )
            )
        }

        return FirefoxDriver(options)
    }

}
