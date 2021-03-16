package selenium.wait

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import org.openqa.selenium.WebElement
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverWaitSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the page title matches <<textMatchExpression>>",
            description = "Wait until the title of the current page matches the given expression, or the timeout is exceeded."
    )
    fun waitForCurrentPageTitleIs(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "waiting for the title of the current page to be\n" +
                "-----------------------------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            TextMatcherService.matches(textMatchExpression, driver.title)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the page title does not match <<textMatchExpression>>",
            description = "Wait until the title of the current page does not match the given expression, or the timeout is exceeded."
    )
    fun waitForCurrentPageTitleIsNot(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "waiting for the title of the current page to NOT be\n" +
                "---------------------------------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            TextMatcherService.doesNotMatch(textMatchExpression, driver.title)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the element <<elementLocator>> is present",
            description = "Wait until the given element is present on the page, or the timeout is exceeded."
    )
    fun waitForElementPresent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "waiting for an element to be present\n" +
                "------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitForElementPresent(elementLocator)
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the element <<elementLocator>> is not present",
            description = "Wait until the given element is not present on the page, or the timeout is exceeded."
    )
    fun waitForElementNotPresent(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "waiting for an element to NOT be present\n" +
                "----------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            ElementLocatorService.locateElement(driver, elementLocator) == null
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the element <<elementLocator>> is visible",
            description = "Wait until the given element is visible on the page, or the timeout is exceeded."
    )
    fun waitForElementVisibile(
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "waiting for an element to be visible\n" +
                "----------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            val element = ElementLocatorService.locateElement(driver, elementLocator)
            element != null && element.isDisplayed
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the element <<elementLocator>> is hidden",
            description = "Wait until the given element is visible on the page, or the timeout is exceeded."
    )
    fun waitForElementHidden (
            @Param(
                    description = SeleniumSharedDescriptions.ELEMENT_LOCATOR_DESCRIPTION
            )
            elementLocator: String
    ) {
        logger.info(
                "waiting for an element to be hidden\n" +
                "----------------------------------------\n" +
                "elementLocator : $elementLocator\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            val element = ElementLocatorService.locateElement(driver, elementLocator)
            element != null && !element.isDisplayed
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the text of element <<elementLocator>> should be <<textMatchExpression>>",
            description = "Wait until the given element text matches the provided expression, or the timeout is exceeded."
    )
    fun waitForTextMatch (
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
                "waiting for element text to match expression\n" +
                "----------------------------------------\n" +
                "elementLocator      : $elementLocator\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            TextMatcherService.matches(textMatchExpression, actualText)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I wait until the text of element <<elementLocator>> should not be <<textMatchExpression>>",
            description = "Wait until the given element text doesn't match the provided expression, or the timeout is exceeded."
    )
    fun waitForTextToNotMatch (
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
                "waiting for element text to not match expression\n" +
                "----------------------------------------\n" +
                "elementLocator      : $elementLocator\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.waitUntil { driver ->
            val element: WebElement = ElementLocatorService.locateElement(driver, elementLocator)
                    ?: throw AssertionError("the element [$elementLocator] should be present on the page, but is not")

            val actualText: String = element.text

            !TextMatcherService.matches(textMatchExpression, actualText)
        }
    }

//----------------------------------------------------------------------------------------------------------------------
//@When(
//    value = "I wait until the JS script <<script>> returns true.",
//    description = "Executes the provided JS script expression in the context of the current selected frame or window. " +
//        "This step will wait until the script returns the value true."
//)
//fun evaluateJavaScriptExpresion(
//    script: String
//) {
//    logger.info(
//        "executing JavaScript script:\n" +
//            "----------------------------\n" +
//            "script : $script\n" +
//            "\n"
//    )
//    WebDriverWait(
//        driver,
//        60
//    ).until(ExpectedCondition<WebElement?> { wd: WebDriver? -> (wd as JavascriptExecutor?)!!.executeScript("return document.getElementById(someid)") })
//
//    webDriverManager.waitUntil {  }
//
//    webDriverManager.executeWebDriverStep { driver ->
//        (driver as JavascriptExecutor).executeScript(script)
//    }
//}

}
