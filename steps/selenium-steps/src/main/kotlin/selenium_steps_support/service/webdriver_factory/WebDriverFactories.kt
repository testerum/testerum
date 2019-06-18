package selenium_steps_support.service.webdriver_factory

import com.testerum.api.test_context.settings.model.SeleniumBrowserType
import selenium_steps_support.service.webdriver_factory.chrome.ChromeWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.EdgeWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.FirefoxWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.InternetExplorerWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.OperaWebDriverFactory
import selenium_steps_support.service.webdriver_factory.chrome.SafariWebDriverFactory

object WebDriverFactories {

    fun getWebDriverFactory(browserType: SeleniumBrowserType): WebDriverFactory = when (browserType) {
        SeleniumBrowserType.CHROME             -> ChromeWebDriverFactory
        SeleniumBrowserType.FIREFOX            -> FirefoxWebDriverFactory
        SeleniumBrowserType.OPERA              -> OperaWebDriverFactory
        SeleniumBrowserType.EDGE               -> EdgeWebDriverFactory
        SeleniumBrowserType.INTERNET_EXPLORER  -> InternetExplorerWebDriverFactory
        SeleniumBrowserType.SAFARI             -> SafariWebDriverFactory
        else -> throw IllegalArgumentException("not yet implemented") // todo: implement for other browsers also
    }

}
