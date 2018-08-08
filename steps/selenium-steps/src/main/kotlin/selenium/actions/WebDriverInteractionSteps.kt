package selenium.actions

import com.testerum.api.annotations.steps.When
import org.openqa.selenium.WebElement
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverInteractionSteps {

    @When("I click the element <<elementLocator>>")
    fun click(elementLocator: String) {
        WebDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            element.click()
        }
    }

}