package selenium.actions

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverNavigationSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

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

    @When(
            value = "I navigate to url <<url>>"
    )
    fun whenINavigateToUrl(url: String) {
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

    @When(
            value = "I navigate to the previous page",
            description = "Goes backward in the browser's history."
    )
    fun navigateToThePreviousPage() {
        logger.info("navigating to the previous page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().back()
        }
    }

    @When(
            value = "I navigate to the next page",
            description = "Goes forward in the browser's history."
    )
    fun navigateToTheNextPage() {
        logger.info("navigating to the next page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().forward()
        }
    }

    @When("I refresh the current page")
    fun refreshTheCurrentPage() {
        logger.info("refreshing the current page\n\n")

        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().refresh()
        }
    }
}
