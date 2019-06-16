package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.api.test_context.settings.model.SeleniumDriverSettingValue
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.WebDriverFactory
import selenium_steps_support.utils.SeleniumStepsDirs
import java.nio.file.Path as JavaPath

object ChromeWebDriverFactory : WebDriverFactory {

    private val LOG: Logger = LoggerFactory.getLogger(ChromeWebDriverFactory::class.java)

    override fun createWebDriver(config: SeleniumDriverSettingValue,
                                 driversByBrowser: SeleniumDriversByBrowser): WebDriver {
        val driverInfo = driversByBrowser.getDriverInfoByBrowserAndDriverVersion(
                browserType = config.browserType,
                driverVersion = config.driverVersion
        ) ?: throw RuntimeException("could not find a Selenium driver for browserType=[${config.browserType}] and driverVersion=[${config.driverVersion}]")

        val driverBinaryPath: JavaPath = SeleniumStepsDirs.getSeleniumDriversDir()
                .resolve(driverInfo.relativePath)
                .toAbsolutePath()
                .normalize()

        LOG.info("using Selenium driver [$driverBinaryPath]")

        // todo: this is nasty: it's a global variable preventing us from using different drivers (e.g. different Chrome versions) at the same time ==> find a better way)
        System.setProperty("webdriver.chrome.driver", driverBinaryPath.toAbsolutePath().toString())

        val options = ChromeOptions()
        options.setHeadless(config.headless)
        if (config.browserExecutablePath != null) {
            LOG.info("using Chrome executable [${config.browserExecutablePath}]")
            options.setBinary(config.browserExecutablePath)
        } else {
            LOG.info("using system Chrome installation")
        }

        return ChromeDriver(options)
    }

}
