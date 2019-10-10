package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.remote.RemoteWebDriver
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import selenium_steps_support.service.webdriver_factory.WebDriverFactory
import java.net.URL

object RemoteWebDriverFactory : WebDriverFactory {

    private val LOG: Logger = LoggerFactory.getLogger(RemoteWebDriverFactory::class.java)

    override fun createWebDriver(config: SeleniumDriverSettingValue,
                                 driversByBrowser: SeleniumDriversByBrowser): WebDriver {
        if (config.remoteUrl == null) {
            throw IllegalArgumentException("when connecting to a remove Selenium driver server, the URL is required")
        }

        LOG.info("using remote Selenium driver server at URL [${config.remoteUrl}]")

        val capabilities = DesiredCapabilities()

        capabilities.isJavascriptEnabled = true

        capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)

        return RemoteWebDriver(
                URL(config.remoteUrl),
                capabilities
        )
    }

}
