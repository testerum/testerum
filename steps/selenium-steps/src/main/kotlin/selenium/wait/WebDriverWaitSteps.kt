package selenium.wait

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.elem_locators.ElementLocatorService
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverWaitSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

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

}
