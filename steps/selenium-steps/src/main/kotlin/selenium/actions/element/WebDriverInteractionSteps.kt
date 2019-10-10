package selenium.actions.element

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager


class WebDriverInteractionSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

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
        logger.info(
                "clicking\n" +
                "--------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            Actions(driver)
                    .moveToElement(element)
                    .click()
                    .build()
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I click the element <<elementLocator>> at <<position>>",
            description = "Clicks at the specified position (X, Y) on a target element (e.g., a link, button, checkbox, or radio button).\n" +
                    "The coordinates are relative to the target element (e.g., 0,0 is the top left corner of the element)."
    )
    fun clickAt(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,
            @Param(
                    description = "Specifies the x,y position (i.e. - 10,20) of the mouse event relative to the element returned by the locator."
            )
            position: String
    ) {
        logger.info(
                "clicking at position\n" +
                "--------------------\n" +
                "elementLocator : $elementLocator\n" +
                "position : $position\n" +
                "\n"
        )

    val split = position.split(",")
    if(split.size != 2) {
        throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
    }

    val x = try {
        split[0].toInt()
    } catch (e: NumberFormatException) {
        throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
    }
    val y = try {
        split[1].toInt()
    } catch (e: NumberFormatException) {
        throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
    }

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            Actions(driver)
                    .moveToElement(element, x, y)
                    .click()
                    .build()
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I double-click the element <<elementLocator>>",
            description = "Simulates a mouse double-click on the given element."
    )
    fun doubleClick(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "double clicking\n" +
                "---------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            Actions(driver)
                    .moveToElement(element)
                    .doubleClick()
                    .build()
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I double-click the element <<elementLocator>> at <<position>>",
            description = "Double=clicks at the specified position (X, Y) on a target element (e.g., a link, button, checkbox, or radio button).\n" +
                    "The coordinates are relative to the target element (e.g., 0,0 is the top left corner of the element)."
    )
    fun doubleClick(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,
            @Param(
                    description = "Specifies the x,y position (i.e. - 10,20) of the mouse event relative to the element returned by the locator."
            )
            position: String
    ) {
        logger.info(
                "double clicking at position\n" +
                        "---------------------------\n" +
                        "elementLocator : $elementLocator\n" +
                        "position : $position\n" +
                        "\n"
        )

        val split = position.split(",")
        if(split.size != 2) {
            throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }

        val x = try {
            split[0].toInt()
        } catch (e: NumberFormatException) {
            throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }
        val y = try {
            split[1].toInt()
        } catch (e: NumberFormatException) {
            throw AssertionError("the position parameter [$position] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            Actions(driver)
                    .moveToElement(element, x, y)
                    .doubleClick()
                    .build()
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I scroll into view the element <<elementLocator>>",
            description = "Changes the scroll bar position to bring the element into the view port of the browser"
    )

    fun scrollToElement(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "scrollToElement\n" +
                "---------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val javascriptExecutor = driver as? JavascriptExecutor
            if (javascriptExecutor != null) {
                javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element)
            } else {
                throw RuntimeException(
                        "the current WebDriver driver cannot execute JavaScript" +
                        ": scrolling an element into view is not possible"
                )
            }
        }
    }
}
