package selenium.actions

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import org.openqa.selenium.WebElement
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverFormSteps {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I type <<text>> into the field <<elementLocator>>",
            description = "Simulates typing keys from the keyboard into the given element."
    )
    fun sendKeys(
            @Param(
                    required = false,
                    description = "The text to type into the given element."
            ) text: String?,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            // todo: parse "text" and send org.openqa.selenium.Keys if needed

            field.clear()
            field.sendKeys(text.orEmpty())
        }
    }

    @When(
            value = "I submit the form containing the field identified by the element locator <<elementLocator>>",
            description = "Finds the nearest ``form`` ancestor of the given element and submits it."
    )
    fun submitTheForm(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            field.submit()
        }
    }

}
