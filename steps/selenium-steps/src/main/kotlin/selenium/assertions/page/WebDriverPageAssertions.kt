package selenium.assertions.page

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverPageAssertions {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the title of the current page should be <<textMatchExpression>>",
            description = "Checks if the title of the current page matches the given expression."
    )
    fun assertCurrentPageTitleShouldBe(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the title of the current page should be\n" +
                "---------------------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.title

            if (TextMatcherService.doesNotMatch(textMatchExpression, actualText)) {
                throw AssertionError("the title of the current page does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the title of the current page should not be <<textMatchExpression>>",
            description = "Checks that the title of the current page does not match the given expression."
    )
    fun assertCurrentPageTitleShouldNotBe(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the title of the current page should NOT be\n" +
                "-------------------------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.title

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the title of the current page matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the url of the current page should be <<textMatchExpression>>",
            description = "Checks if the current page URL matches the given expression."
    )
    fun assertCurrentUrlShouldBe(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the current url should be\n" +
                "-------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.currentUrl

            if (TextMatcherService.doesNotMatch(textMatchExpression, actualText)) {
                throw AssertionError("the url of the current page does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

//----------------------------------------------------------------------------------------------------------------------
    @Then(
            value = "the url of the current page should not be <<textMatchExpression>>",
            description = "Checks that the current page URL does not match the given expression."
    )
    fun assertCurrentUrlShouldNotBe(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            textMatchExpression: String
    ) {
        logger.info(
                "the current url should NOT be\n" +
                "-----------------------------\n" +
                "textMatchExpression : $textMatchExpression\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val actualText: String = driver.currentUrl

            if (TextMatcherService.matches(textMatchExpression, actualText)) {
                throw AssertionError("the url of the current page matches: expected not to match [$textMatchExpression] but was [$actualText]")
            }
        }
    }

}
