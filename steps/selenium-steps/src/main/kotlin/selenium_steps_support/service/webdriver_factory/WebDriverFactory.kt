package selenium_steps_support.service.webdriver_factory

import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.openqa.selenium.WebDriver

interface WebDriverFactory {

    fun createWebDriver(config: SeleniumDriverSettingValue,
                        driversByBrowser: SeleniumDriversByBrowser): WebDriver

}
