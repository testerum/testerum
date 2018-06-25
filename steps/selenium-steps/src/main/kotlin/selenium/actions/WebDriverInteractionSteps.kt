package selenium.actions

import com.testerum.api.annotations.steps.When
import org.openqa.selenium.WebElement
import org.springframework.beans.factory.annotation.Autowired
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverInteractionSteps @Autowired constructor(private val webDriverManager: WebDriverManager) {

    @When("I click the element <<elementLocator>>")
    fun click(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            element.click()
        }
    }

}