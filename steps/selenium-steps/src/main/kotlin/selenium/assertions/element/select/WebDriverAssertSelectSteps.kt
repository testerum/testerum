package selenium.assertions.element.select

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverAssertSelectSteps {
    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Then (
            value = "the option <<optionLocator>> from the select element <<elementLocator>> should be selected",
            description = "Checks if an element from a drop-down or multi-select is selected based on an option locator.\n" +
                          "Option locators provide different ways of specifying a select element (e.g., label=, value=, index=).\n" +
                          "If no option locator prefix is provided, a match on the label will be attempted."
    )
    fun isOptionSelected (
            @Param(
                    description = "This parameter provide different ways to target an select option (e.g., label=, value=, index=).\n" +
                                  "If no option locator prefix is provided, a match on the label will be attempted."
            )
            optionLocator: String,
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "check if item in drop-down or multi-select element is selected\n" +
                "--------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "optionLocator  : $optionLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val select = Select(field)

            val allSelectedOptions = select.allSelectedOptions

            if (optionLocator.startsWith("index=")) {
                val itemIndexAsString = optionLocator.removePrefix("index=")
                val itemIndex = try {
                     itemIndexAsString.toInt()
                } catch (e: Exception) {
                    throw AssertionError("the index value [$elementLocator] should be a number")
                }

                if (itemIndex > select.options.size) {
                    throw AssertionError("the index value [$elementLocator] is bigger then the number of select options")
                }

                val indexText = select.options[itemIndex].getAttribute("value");
                val isItemWithTextSelected = isItemWithTextSelected(indexText, allSelectedOptions)

                if (isItemWithTextSelected) {
                    return@executeWebDriverStep;
                } else {
                    throw AssertionError("the option at index [$itemIndex] should be selected, but is not")
                }
            }

            for (selectedOption in allSelectedOptions) {
                val actualLabel = selectedOption.getText()
                if (optionLocator.startsWith("label=")) {
                    val expectedLabel = optionLocator.removePrefix("label=")
                    if(expectedLabel == actualLabel) {
                        return@executeWebDriverStep;
                    }
                }

                if (optionLocator.startsWith("value=")) {
                    val actualValue = selectedOption.getAttribute("value")
                    val expectedValue = optionLocator.removePrefix("value=")
                    if(expectedValue == actualValue) {
                        return@executeWebDriverStep;
                    }
                }

                val expectedLabel = optionLocator
                if(expectedLabel == actualLabel) {
                    return@executeWebDriverStep;
                }
            }

            if (optionLocator.startsWith("label=")) {
                val itemLabel = optionLocator.removePrefix("label=")
                throw AssertionError("the option with label [$itemLabel] should be selected, but is not")
            }
            if (optionLocator.startsWith("value=")) {
                val itemValue = optionLocator.removePrefix("value=")
                throw AssertionError("the option with value [$itemValue] should be selected, but is not")
            }
            throw AssertionError("the option with label [$optionLocator] should be selected, but is not")
        }
    }

    private fun isItemWithTextSelected(indexText: String?, allSelectedOptions: List<WebElement>): Boolean {
        for (selectedOption in allSelectedOptions) {
            val selectedOptionText = selectedOption.getAttribute("value");
            if (indexText == selectedOptionText) {
                return true;
            }
        }

        return false;
    }

//======================================================================================================================
    @Then (
            value = "the option <<optionLocator>> from the select element <<elementLocator>> should not be selected",
            description = "Checks if an element from a drop-down or multi-select is NOT selected based on an option locator.\n" +
                    "Option locators provide different ways of specifying a select element (e.g., label=, value=, index=).\n" +
                    "If no option locator prefix is provided, a match on the label will be attempted."
    )
    fun isOptionNotSelected (
            @Param(
                    description = "This parameter provide different ways to target an select option (e.g., label=, value=, index=).\n" +
                            "If no option locator prefix is provided, a match on the label will be attempted."
            )
            optionLocator: String,
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "check if item in drop-down or multi-select element is not selected\n" +
                "--------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "optionLocator  : $optionLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val select = Select(field)

            val allSelectedOptions = select.allSelectedOptions

            if (optionLocator.startsWith("index=")) {
                val itemIndexAsString = optionLocator.removePrefix("index=")
                val itemIndex = try {
                    itemIndexAsString.toInt()
                } catch (e: Exception) {
                    throw AssertionError("the index value [$elementLocator] should be a number")
                }

                val indexText = select.options[itemIndex].getAttribute("value");
                val isItemWithTextSelected = isItemWithTextSelected(indexText, allSelectedOptions)

                if (!isItemWithTextSelected) {
                    return@executeWebDriverStep;
                } else {
                    throw AssertionError("the option at index [$itemIndex] should be NOT selected, but it is")
                }
            }

            for (selectedOption in allSelectedOptions) {
                val actualLabel = selectedOption.getText()
                if (optionLocator.startsWith("label=")) {
                    val expectedLabel = optionLocator.removePrefix("label=")
                    if(expectedLabel == actualLabel) {
                        throw AssertionError("the option with label [$expectedLabel] should be selected, but is not")
                    }
                }

                if (optionLocator.startsWith("value=")) {
                    val actualValue = selectedOption.getAttribute("value")
                    val expectedValue = optionLocator.removePrefix("value=")
                    if(expectedValue == actualValue) {
                        throw AssertionError("the option with value [$expectedValue] should be selected, but is not")
                    }
                }

                val expectedLabel = optionLocator
                if(expectedLabel == actualLabel) {
                    throw AssertionError("the option with label [$expectedLabel] should be selected, but is not")
                }
            }
        }
    }

}
