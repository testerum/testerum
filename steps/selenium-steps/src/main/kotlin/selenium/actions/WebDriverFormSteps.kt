package selenium.actions

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverFormSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

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
        logger.info(
                "sending keys\n" +
                "------------\n" +
                "text           : $text\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            // todo: parse "text" and send org.openqa.selenium.Keys if needed

            field.clear()

            (driver as? JavascriptExecutor)?.executeScript("arguments[0].value=''", field)

            field.sendKeys(text.orEmpty())
        }
    }

    @When(
            value = "I submit the form containing the field <<elementLocator>>",
            description = "Finds the nearest ``form`` ancestor of the given element and submits it."
    )
    fun submitTheForm(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "submitting the form containing element\n" +
                "--------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            field.submit()
        }
    }

}
