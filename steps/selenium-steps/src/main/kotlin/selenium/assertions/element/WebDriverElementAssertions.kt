package selenium.assertions.element

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.WebElement
import selenium_steps_support.service.css_class.CssClassAttributeParser
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverElementAssertions {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should be present",
            description = "Checks if the given element is not absent from the page."
    )
    fun assertElementPresent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "the element should be present\n" +
                "-----------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement? = ElementLocatorService.locateElement(driver, elementLocator)
            if (element == null) {
                throw AssertionError("the element [$elementLocator] should be present on the page, but is not")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should be absent",
            description = "Checks if the given element is not present on the page."
    )
    fun assertElementAbsent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "the element should be absent\n" +
                "----------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement? = ElementLocatorService.locateElement(driver, elementLocator)
            if (element != null) {
                throw AssertionError("the element [$elementLocator] should absent from the page, but is not")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should be displayed",
            description = "Checks if the given element is \"displayed\" on the page, for example by not having it's ``display`` CSS property set to ``none``."
    )
    fun assertElementDisplayed(elementLocator: String) {
        logger.info(
                "the element should be displayed\n" +
                "-------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                    "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (!element.isDisplayed) {
                throw AssertionError("the element [$elementLocator] should be displayed, but is not")
            }
        }
    }

//======================================================================================================================
    /**
     * check that an element is hidden (e.g. "display: none", or "visibility: hidden")
     */
    @Then(
            value = "the element <<elementLocator>> should be hidden",
            description = "Checks if the given element is not \"displayed\" on the page, for example by having it's ``display`` CSS property set to ``none``. "
    )
    fun assertElementHidden(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "the element should be hidden\n" +
                "----------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (element.isDisplayed) {
                throw AssertionError("the element [$elementLocator] should be hidden, but is not")
            }
        }
    }

//======================================================================================================================
    @Then(
            value = "the element <<elementLocator>> should be enabled",
            description = "Checks that the given element is enabled. This is generally true for everything except disabled input elements."
    )
    fun assertElementEnabled(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "the element should be enabled\n" +
                "-----------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (!element.isEnabled) {
                throw AssertionError("the element [$elementLocator] should be enabled, but is not")
            }
        }
    }

//======================================================================================================================
    @Then(
            value = "the element <<elementLocator>> should be disabled",
            description = "Checks that the given element is disabled. This is generally false for everything except disabled input elements."
    )
    fun assertElementDisabled(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "the element should be disabled\n" +
                "------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (element.isEnabled) {
                throw AssertionError("the element [$elementLocator] should be disabled, but is not")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should have the value <<expectedValueExpression>>",
            description = "Checks that the given element has a ``value`` attribute matching the given expression."
    )
    fun assertThatTheElementHasValue(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            expectedValueExpression: String
    ) {
        logger.info(
                "the element should have the value\n" +
                "---------------------------------\n" +
                "elementLocator          : $elementLocator\n" +
                "expectedValueExpression : $expectedValueExpression\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val actualValue: String = field.getAttribute("value")
                    ?: throw AssertionError("field [$elementLocator] does not have a value")

            if (TextMatcherService.doesNotMatch(expectedValueExpression, actualValue)) {
                throw AssertionError("field [$elementLocator] has unexpected value: expected: [$expectedValueExpression] but was: [$actualValue]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should have the attribute <<attributeName>> with a value of <<attributeValueTextMatchExpression>>",
            description = "Checks that the given element has an attribute with the given name whose value matches the given expression."
    )
    fun assertElementAttributeShouldBe(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = "the name of the attribute to check"
            )
            attributeName: String,

            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            attributeValueTextMatchExpression: String
    ) {
        logger.info(
                "the element should have the attribute with the value\n" +
                "----------------------------------------------------\n" +
                "elementLocator                    : $elementLocator\n" +
                "attributeName                     : $attributeName\n" +
                "attributeValueTextMatchExpression : $attributeValueTextMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualValue: String = element.getAttribute(attributeName)
                    ?: throw AssertionError("the element [$elementLocator] should have an attribute [$attributeName] with a value, but either the element doesn't have that attribute, or the attribute doesn't have a value")

            if (TextMatcherService.doesNotMatch(attributeValueTextMatchExpression, actualValue)) {
                throw AssertionError("the value of attribute [$attributeName] of element [$elementLocator] does not match: expected [$attributeValueTextMatchExpression] but was [$actualValue]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should have the attribute <<attributeName>> with a value different from <<attributeValueTextMatchExpression>>",
            description = "Checks that the given element has an attribute with the given name whose value does not match the given expression."
    )
    fun assertElementAttributeShouldNotBe(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = "the name of the attribute to check"
            )
            attributeName: String,

            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            attributeValueTextMatchExpression: String
    ) {
        logger.info(
                "the element should have the attribute with the value DIFFERENT FROM\n" +
                "-------------------------------------------------------------------\n" +
                "elementLocator                    : $elementLocator\n" +
                "attributeName                     : $attributeName\n" +
                "attributeValueTextMatchExpression : $attributeValueTextMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualValue: String = element.getAttribute(attributeName)
                    ?: return@executeWebDriverStep

            if (TextMatcherService.matches(attributeValueTextMatchExpression, actualValue)) {
                throw AssertionError("the value of attribute [$attributeName] of element [$elementLocator] matches: expected not to match [$attributeValueTextMatchExpression] but was [$actualValue]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should have the CSS class <<cssClass>>",
            description = "Checks that the given element has the given class in its list of CSS classes." +
                          "Note that it's OK if the element has other CSS classes in addition to the given one."
    )
    fun assertElementCssClassPresent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = "the name of the CSS class to check"
            )
            cssClass: String
    ) {
        logger.info(
                "the element should have the CSS class\n" +
                "-------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "cssClass       : $cssClass\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val cssClassAttrValue: String = element.getAttribute("class")
            val cssClasses: Set<String> = CssClassAttributeParser.parse(cssClassAttrValue)

            if (!cssClasses.contains(cssClass)) {
                throw AssertionError("the element [$elementLocator] does not have the CSS class [$cssClass], but it was expected to have it")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the element <<elementLocator>> should not have the CSS class <<cssClass>>",
            description = "Checks that the given element does not have the given class in its list of CSS classes."
    )
    fun assertElementCssClassAbsent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = "the name of the CSS class to check"
            )
            cssClass: String
    ) {
        logger.info(
                "the element should NOT have the CSS class\n" +
                "-----------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "cssClass       : $cssClass\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val cssClassAttrValue: String = element.getAttribute("class")
            val cssClasses: Set<String> = CssClassAttributeParser.parse(cssClassAttrValue)

            if (cssClasses.contains(cssClass)) {
                throw AssertionError("the element [$elementLocator] has the CSS class [$cssClass], but it was expected not to have it")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the text of element <<elementLocator>> should be <<textMatchExpression>>",
            description = "Checks that the text of the given element and sub-elements match the given expression." +
                          "Note that only the visible text (i.e. not hidden by CSS) is considered."
    )
    fun assertElementTextShouldBe(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the element should have the text\n" +
                "--------------------------------\n" +
                "elementLocator      : $elementLocator\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            if (TextMatcherService.doesNotMatch(textMatchExpression, actualText)) {
                throw AssertionError("the text of element [$elementLocator] does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the text of element <<elementLocator>> should not be <<textMatchExpression>>",
            description = "Checks that the text of the given element and sub-elements does not match the given expression." +
                          "Note that only the visible text (i.e. not hidden by CSS) is considered."
    )
    fun assertElementTextShouldNotBe(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String,

            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the element should NOT have the text\n" +
                "------------------------------------\n" +
                "elementLocator      : $elementLocator\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the text of element [$elementLocator] matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }

//======================================================================================================================
    @Then(
            value = "the element <<elementLocator>> is checked",
            description = "Confirm that the target element has been checked. The test will stop if the assert fails."
    )
    fun assertsThatElementIsChecked (
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "assert that element is checked (checkbox / radio)\n" +
                        "-------------------------------------------------\n" +
                        "elementLocator : $elementLocator\n" +
                        "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            if (!field.isSelected) {
                throw AssertionError("the element [$elementLocator] should be checked, but is not")
            }
        }
    }

//======================================================================================================================
    @Then(
            value = "the element <<elementLocator>> is not checked",
            description = "Confirm that the target element is not checked. The test will stop if the assert fails."
    )
    fun assertsThatElementIsNotChecked (
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "assert that element is not checked (checkbox / radio)\n" +
                        "-------------------------------------------------\n" +
                        "elementLocator : $elementLocator\n" +
                        "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            if (field.isSelected) {
                throw AssertionError("the element [$elementLocator] should not be checked, but is not")
            }
        }
    }

}
