package selenium.assertions.element

import com.testerum.api.annotations.steps.Then
import org.openqa.selenium.WebElement
import selenium_steps_support.service.css_class.CssClassAttributeParser
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverElementAssertions {

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Then("the element <<elementLocator>> should be present")
    fun assertElementPresent(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement? = ElementLocatorService.locateElement(driver, elementLocator)
            if (element == null) {
                throw AssertionError("the element [$elementLocator] should be present on the page, but is not")
            }
        }
    }

    @Then("the element <<elementLocator>> should be absent")
    fun assertElementAbsent(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement? = ElementLocatorService.locateElement(driver, elementLocator)
            if (element != null) {
                throw AssertionError("the element [$elementLocator] should absent from the page, but is not")
            }
        }
    }

    @Then("the element <<elementLocator>> should be displayed")
    fun assertElementDisplayed(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (!element.isDisplayed) {
                throw AssertionError("the element [$elementLocator] should be displayed, but is not")
            }
        }
    }

    /**
     * check that an element is hidden (e.g. "display: none", or "visibility: hidden")
     */
    @Then("the element <<elementLocator>> should be hidden")
    fun assertElementHidden(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (element.isDisplayed) {
                throw AssertionError("the element [$elementLocator] should be hidden, but is not")
            }
        }
    }

    @Then("the element <<elementLocator>> should be enabled")
    fun assertElementEnabled(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (!element.isEnabled) {
                throw AssertionError("the element [$elementLocator] should be enabled, but is not")
            }
        }
    }

    @Then("the element <<elementLocator>> should be disabled")
    fun assertElementDisabled(elementLocator: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            if (element.isEnabled) {
                throw AssertionError("the element [$elementLocator] should be disabled, but is not")
            }
        }
    }

    @Then("the element <<elementLocator>> should have the value <<expectedValue>>")
    fun assertThatTheElementHasValue(elementLocator: String, expectedValue: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val field: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the field [$elementLocator] should be present on the page, but is not")

            val actualValue: String? = field.getAttribute("value")

            // todo: use text matching
            if (expectedValue != actualValue) {
                throw AssertionError("field [$elementLocator] has unexpected value: expected: [$expectedValue] but was: [$actualValue]")
            }
        }
    }

    @Then("the element <<elementLocator>> should have the attribute <<attributeName>> with a value of <<attributeValueTextMatchExpression>>")
    fun assertElementAttributeShouldBe(elementLocator: String, attributeName: String, attributeValueTextMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualValue: String = element.getAttribute(attributeName)
                    ?: throw AssertionError("the element [$elementLocator] should have an attribute [$attributeName] with a value, but either the element doesn't have that attribute, or the attribute doesn't have a value")

            if (!TextMatcherService.matches(attributeValueTextMatchExpression, actualValue)) {
                throw AssertionError("the value of attribute [$attributeName] of element [$elementLocator] does not match: expected [$attributeValueTextMatchExpression] but was [$actualValue]")
            }
        }
    }

    @Then("the element <<elementLocator>> should have the attribute <<attributeName>> with a value different from <<attributeValueTextMatchExpression>>")
    fun assertElementAttributeShouldNotBe(elementLocator: String, attributeName: String, attributeValueTextMatchExpression: String) {
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

    @Then("the element <<elementLocator>> should have the CSS class <<cssClass>>")
    fun assertElementCssClassPresent(elementLocator: String, cssClass: String) {
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

    @Then("the element <<elementLocator>> should not have the CSS class <<cssClass>>")
    fun assertElementCssClassAbsent(elementLocator: String, cssClass: String) {
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

    @Then("the text of element <<elementLocator>> should be <<textMatchExpression>>")
    fun assertElementTextShouldBe(elementLocator: String, textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            if (!TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the text of element [$elementLocator] does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

    @Then("the text of element <<elementLocator>> should not be <<textMatchExpression>>")
    fun assertElementTextShouldNotBe(elementLocator: String, textMatchExpression: String) {
        webDriverManager.executeWebDriverStep { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the text of element [$elementLocator] matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }
}
