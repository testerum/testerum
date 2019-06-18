package selenium_steps_support.service.webdriver_factory

import com.testerum.api.test_context.settings.model.SeleniumBrowserType
import selenium_steps_support.service.webdriver_factory.chrome.ChromeWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.EdgeWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.FirefoxWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.OperaWebDriverFactory

object WebDriverFactories {

    fun getWebDriverFactory(browserType: SeleniumBrowserType): WebDriverFactory = when (browserType) {
        SeleniumBrowserType.CHROME  -> ChromeWebDriverFactory
        SeleniumBrowserType.FIREFOX -> FirefoxWebDriverFactory
        SeleniumBrowserType.OPERA   -> OperaWebDriverFactory
        SeleniumBrowserType.EDGE    -> EdgeWebDriverFactory
        else -> throw IllegalArgumentException("not yet implemented") // todo: implement for other browsers also}
    }

}
