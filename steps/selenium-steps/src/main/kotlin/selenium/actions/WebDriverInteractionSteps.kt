package selenium.actions

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import org.openqa.selenium.WebElement
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverInteractionSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I click the element <<elementLocator>>",
            description = "Simulates a mouse click on the given element."
    )
    fun click(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            element.click()
        }
    }

}
