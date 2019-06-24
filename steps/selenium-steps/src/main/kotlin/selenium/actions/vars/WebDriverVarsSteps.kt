package selenium.actions.vars

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import org.openqa.selenium.WebElement
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverVarsSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I save into the variable <<varName>> the text of element <<elementLocator>>",
            description = "This step extracts the inner text of the given element and saves it into a variable."
    )
    fun saveElementTextIntoVariable(
            @Param(
                    required = false,
                    description = "The name of the variable that will be defined."
            ) varName: String,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "save variable with the element text\n" +
                "------------\n" +
                "varName        : $varName\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            logger.info("Declaring variable: $varName=$actualText\n")

            variables[varName] = actualText;
        }
    }


    @When(
            value = "I save into the variable <<varName>> the value of element <<elementLocator>>",
            description = "This step extracts the value of the given element and saves it into a variable."
    )
    fun saveElementValueIntoVariable(
            @Param(
                    required = false,
                    description = "The name of the variable that will be defined."
            ) varName: String,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "save variable with the element value\n" +
                "------------\n" +
                "varName        : $varName\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val actualValue: String = field.getAttribute("value")
                    ?: throw AssertionError("field [$elementLocator] does not have a value")

            logger.info("Declaring variable: $varName=$actualValue\n")

            variables[varName] = actualValue;
        }
    }
}
