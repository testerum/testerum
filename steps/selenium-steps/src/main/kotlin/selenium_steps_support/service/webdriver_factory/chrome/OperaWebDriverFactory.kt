package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.WebDriver
import org.openqa.selenium.opera.OperaDriver
import org.openqa.selenium.opera.OperaOptions
import org.openqa.selenium.remote.CapabilityType
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.WebDriverFactory
import selenium_steps_support.utils.SeleniumStepsDirs
import java.nio.file.Path as JavaPath

object OperaWebDriverFactory : WebDriverFactory {

    private val LOG: Logger = LoggerFactory.getLogger(OperaWebDriverFactory::class.java)

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

            // todo: this is nasty: it's a global variable preventing us from using different drivers (e.g. different Opera versions) at the same time
            // ==> find a better way
            System.setProperty("webdriver.opera.driver", driverBinaryPath.toAbsolutePath().toString())
        }

        val options = OperaOptions()
        if (config.browserExecutablePath != null) {
            LOG.info("using Opera executable [${config.browserExecutablePath}]")
            options.setBinary(config.browserExecutablePath)
        } else {
            LOG.info("using system Opera installation")
        }

        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)

        if (!webDriverCustomizationScript.isNullOrBlank()) {
            ExpressionEvaluator.evaluate(
                expression = webDriverCustomizationScript,
                context = mapOf(
                    "capabilities" to options
                )
            )
        }

        return OperaDriver(options)
    }

}
