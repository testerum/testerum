package selenium.actions.element

import com.google.common.base.Splitter
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebElement
import org.openqa.selenium.interactions.Actions
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverFormSteps {

    companion object {
        private val KEYS_MAP: Map<String, Keys> = run {
            val keysMap = mutableMapOf<String, Keys>()

            for (enumConstant in Keys::class.java.enumConstants) {
                keysMap.put(enumConstant.name, enumConstant)
            }

            return@run keysMap
        }

        private fun lookupKey(key: String): CharSequence {
            return KEYS_MAP[key] ?: key
        }

        private val SPECIAL_KEYS_SPLITTER = Splitter.on("+")
                .omitEmptyStrings()
    }

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I clear the text from the field <<elementLocator>>",
            description = "Sets the value of the given field to the empty string."
    )
    fun clearFieldValue(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "clearing field value\n" +
                        "--------------------\n" +
                        "elementLocator : $elementLocator\n" +
                        "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            field.clear()

            (driver as? JavascriptExecutor)?.executeScript("arguments[0].value=''", field)

            field.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END))
            field.sendKeys(Keys.DELETE)
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I set <<text>> as the value of the field <<elementLocator>>",
            description = "Sets the value of the given field, clearing any previous value if needed."
    )
    fun setFieldValue(
            @Param(
                    required = false,
                    description = "The text to set as the value of the given field."
            ) text: String?,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "setting field value\n" +
                        "-------------------\n" +
                        "text           : $text\n" +
                        "elementLocator : $elementLocator\n" +
                        "\n"
        )
        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            field.clear()

            (driver as? JavascriptExecutor)?.executeScript("arguments[0].value=''", field)

            field.sendKeys(Keys.HOME, Keys.chord(Keys.SHIFT, Keys.END))
            field.sendKeys(Keys.DELETE)

            if (text != null) {
                field.sendKeys(text)
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I type <<text>> into the field <<elementLocator>>",
            description = "Simulates typing keys from the keyboard into the given field."
    )
    fun typeTextIntoField(
            @Param(
                    required = false,
                    description = "The text to type into the given field."
            ) text: String?,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "typing text into field\n" +
                        "----------------------\n" +
                        "text           : $text\n" +
                        "elementLocator : $elementLocator\n" +
                        "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            if (text != null) {
                field.sendKeys(text)
            }
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I press the special keys <<keysExpression>> on the element <<elementLocator>>",
            description = "Simulates pressing keys or key combinations from the keyboard when the given element is active."
    )
    fun pressSpecialKeys(
            // todo: documentation
            keysExpression: String,

            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "sending the keys expression\n" +
                "------------\n" +
                "keysExpression : $keysExpression\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val expressionKeys = SPECIAL_KEYS_SPLITTER.split(keysExpression)
                    .toList()
                    .map { lookupKey(it) }

            field.sendKeys(*expressionKeys.toTypedArray())
        }
    }

    //----------------------------------------------------------------------------------------------------------------------
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

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I move the mouse pointer to the element <<elementLocator>>",
            description = "Finds the specified element and simulate mouse hover effect."
    )
    fun hoverOver(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "moving the mouse pointer to the element\n" +
                "---------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            Actions(driver)
                    .moveToElement(element)
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I drag the element <<elementLocator>> at position <<elementPosition>>",
            description = "This step scrolls the draggable element in view, clicks, holds it and then releases it at the given position."
    )
    fun dragAndDropBy(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,
            @Param(
                    description = "Specifies the x,y position (i.e. - 10,20) where to drop the dragged element."
            )
            elementPosition: String
    ) {
        logger.info(
                "dragging an element on destination at position\n" +
                "----------------------------------------------\n" +
                "elementLocator: $elementLocator\n" +
                "elementPosition: $elementPosition\n" +
                "\n"
        )

        val split = elementPosition.split(",")
        if (split.size != 2) {
            throw AssertionError("the position parameter [$elementPosition] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }

        val x = try {
            split[0].toInt()
        } catch (e: NumberFormatException) {
            throw AssertionError("the position parameter [$elementPosition] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }
        val y = try {
            split[1].toInt()
        } catch (e: NumberFormatException) {
            throw AssertionError("the position parameter [$elementPosition] is not a valid. The parameter format should be two numbers separated by comma. (i.e. - 10,20)")
        }

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            (driver as JavascriptExecutor).executeScript("arguments[0].scrollIntoView(true);", element)

            Actions(driver)
                    .dragAndDropBy(element, x, y)
                    .build()
                    .perform()
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I drag the element <<elementLocator>> on destination <<destinationElementLocator>>",
            description = "This step scrolls the draggable element in view, clicks, holds it and then releases it on the droppable element."
    )
    fun dragAndDrop(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            destinationElementLocator: String

    ) {
        logger.info(
                "dragging an element on destination at position\n" +
                "----------------------------------------------\n" +
                "elementLocator: $elementLocator\n" +
                "destinationLocator: $destinationElementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.waitForElementPresent(destinationElementLocator)

        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val destinationElement: WebElement = ElementLocatorService.locateElement(driver, destinationElementLocator)
                    ?: throw AssertionError("the element [$destinationElementLocator] should be present on the page, but is not")

            (driver as JavascriptExecutor).executeScript("arguments[0].scrollIntoView(true);", element)

            Actions(driver)
                    .dragAndDrop(element, destinationElement)
                    .build()
                    .perform()
        }
    }
}
