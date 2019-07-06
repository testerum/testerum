package selenium.actions.select

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.Select
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverSelectFormSteps {
    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I select <<optionLocator>> from the select element <<elementLocator>>",
            description = "Select an element from a drop-down or multi-select using an option locator.\n" +
                    "Option locators provide different ways of specifying a select element (e.g., label=, value=, index=).\n" +
                    "If no option locator prefix is provided, a match on the label will be attempted."
    )
    fun selectItem(
            @Param(
                    description = "This parameter provide different ways of specifying the element to select (e.g., label=, value=, index=).\n" +
                            "If no option locator prefix is provided, a match on the label will be attempted."
            )
            optionLocator: String,
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "select an item in drop-down or multi-select element\n" +
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
            if (optionLocator.startsWith("label=")) {
                val itemLabel = optionLocator.removePrefix("label=")
                select.selectByVisibleText(itemLabel)
                return@executeWebDriverStep;
            }
            if (optionLocator.startsWith("value=")) {
                val itemValue = optionLocator.removePrefix("value=")
                select.selectByValue(itemValue)
                return@executeWebDriverStep;
            }
            if (optionLocator.startsWith("index=")) {
                val itemIndexAsString = optionLocator.removePrefix("index=")
                try {
                    val itemIndex = itemIndexAsString.toInt()
                    select.selectByIndex(itemIndex)
                } catch (e: Exception) {
                    throw AssertionError("the index value [$elementLocator] should be a number")
                }
                return@executeWebDriverStep;
            }
            select.selectByVisibleText(optionLocator)
        }
    }

    //======================================================================================================================
    @When(
            value = "I deselect <<optionLocator>> from the select element <<elementLocator>>",
            description = "Deselect an element from a drop-down or multi-select using an option locator.\n" +
                    "Option locators provide different ways of specifying a select element (e.g., label=, value=, index=).\n" +
                    "If no option locator prefix is provided, a match on the label will be attempted."
    )
    fun deselectSelectItem(
            @Param(
                    description = "This parameter provide different ways of specifying the element to select (e.g., label=, value=, index=).\n" +
                            "If no option locator prefix is provided, a match on the label will be attempted."
            )
            optionLocator: String,
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "deselect an item in drop-down or multi-select element\n" +
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
            if (optionLocator.startsWith("label=")) {
                val itemLabel = optionLocator.removePrefix("label=")
                select.selectByVisibleText(itemLabel)
                return@executeWebDriverStep;
            }
            if (optionLocator.startsWith("value=")) {
                val itemValue = optionLocator.removePrefix("value=")
                select.selectByValue(itemValue)
                return@executeWebDriverStep;
            }
            if (optionLocator.startsWith("index=")) {
                val itemIndexAsString = optionLocator.removePrefix("index=")
                try {
                    val itemIndex = itemIndexAsString.toInt()
                    select.selectByIndex(itemIndex)
                } catch (e: Exception) {
                    throw AssertionError("the index value [$elementLocator] should be a number")
                }
                return@executeWebDriverStep;
            }
            select.selectByVisibleText(optionLocator)
        }
    }
}

