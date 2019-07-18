package selenium.actions

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverNavigationSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

//----------------------------------------------------------------------------------------------------------------------
    @Given(
            value = "the page at url <<url>> is open",
            description = "Navigates to the given URL."
    )
    fun givenThePageAtUrlIsOpen(url: String) {
        logger.info(
                "opening page\n" +
                        "------------\n" +
                        "url : $url\n" +
                        "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().to(url)
        }
    }
//----------------------------------------------------------------------------------------------------------------------
}
