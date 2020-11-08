package selenium_steps_support.service.webdriver_factory

import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import org.openqa.selenium.WebDriver

interface WebDriverFactory {

    fun createWebDriver(
        config: SeleniumDriverSettingValue,
        webDriverCustomizationScript: String?,
        driversByBrowser: SeleniumDriversByBrowser
    ): WebDriver

}
