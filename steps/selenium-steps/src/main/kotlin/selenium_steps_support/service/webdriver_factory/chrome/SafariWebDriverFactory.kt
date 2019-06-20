package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.api.test_context.settings.model.SeleniumDriverSettingValue
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.openqa.selenium.WebDriver
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.safari.SafariOptions
import selenium_steps_support.service.webdriver_factory.WebDriverFactory
import java.nio.file.Path as JavaPath

object SafariWebDriverFactory : WebDriverFactory {

    override fun createWebDriver(config: SeleniumDriverSettingValue,
                                 driversByBrowser: SeleniumDriversByBrowser): WebDriver {
        val options = SafariOptions()

        return SafariDriver(options)
    }

}
