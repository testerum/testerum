package selenium.wait

import com.testerum.api.annotations.steps.When
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverWaitSteps {

    @When("I wait until the page title matches <<textMatchExpression>>")
    fun waitForCurrentPageTitleIs(textMatchExpression: String) {
        WebDriverManager.waitUntil { driver ->
            TextMatcherService.matches(textMatchExpression, driver.title)
        }
    }

    @When("I wait until the page title does not match <<textMatchExpression>>")
    fun waitForCurrentPageTitleIsNot(textMatchExpression: String) {
        WebDriverManager.waitUntil { driver ->
            !TextMatcherService.matches(textMatchExpression, driver.title)
        }
    }

    @When("I wait until the element <<elementLocator>> is present")
    fun waitForElementPresent(elementLocator: String) {
        WebDriverManager.waitUntil { driver ->
            ElementLocatorService.locateElement(driver, elementLocator) != null
        }
    }

    @When("I wait until the element <<elementLocator>> is not present")
    fun waitForElementNotPresent(elementLocator: String) {
        WebDriverManager.waitUntil { driver ->
            ElementLocatorService.locateElement(driver, elementLocator) == null
        }
    }

}