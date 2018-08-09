package selenium.actions

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.When
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverNavigationSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Given("the page at url <<url>> is opened")
    fun givenThePageAtUrlIsOpen(url: String) {
        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().to(url)
        }
    }

    @When("I navigate to url <<url>>")
    fun whenINavigateToUrl(url: String) {
        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().to(url)
        }
    }

    @When("I navigate to the previous page")
    fun navigateToThePreviousPage() {
        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().back()
        }
    }

    @When("I navigate to the next page")
    fun navigateToTheNextPage() {
        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().forward()
        }
    }

    @When("I refresh the current page")
    fun refreshTheCurrentPage() {
        webDriverManager.executeWebDriverStep { driver ->
            driver.navigate().refresh()
        }
    }
}