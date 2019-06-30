package selenium.assertions.popup

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.NoAlertPresentException
import org.openqa.selenium.support.ui.ExpectedConditions
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager


class WebDriverPopupAssertions {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Then(
            value = "an alert, confirm or prompt should be present",
            description = "Checks if an alert popup has been showed."
    )
    fun assertAlertIsPresent() {
        logger.info(
                "an alert should be present"
        )

        webDriverManager.executeWebDriverStep { driver ->
            webDriverManager.waitUntil { driver ->
                ExpectedConditions.alertIsPresent() != null
            }

            try {
                driver.switchTo().alert();
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }
        }
    }

    @Then(
            value = "the alert, confirm or prompt message should be <<alertMessage>>",
            description = "Checks if the alert, confirm or prompt popup has the provided message."
    )
    fun assertAlertMessage(
            @Param(
                    description = SeleniumSharedDescriptions.TEXT_MATCH_EXPRESSION_DESCRIPTION
            )
            expectedValueExpression: String
    ) {
        logger.info(
                "the alert, confirm or prompt should have the message\n" +
                "---------------------------------\n" +
                "expectedValueExpression : $expectedValueExpression\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            webDriverManager.waitUntil { driver ->
                ExpectedConditions.alertIsPresent() != null
            }

            val actualMessage = try {
                 driver.switchTo().alert().text;
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }


            if (TextMatcherService.doesNotMatch(expectedValueExpression, actualMessage)) {
                throw AssertionError("alert has an unexpected message: expected: [$expectedValueExpression] but was: [$actualMessage]")
            }
        }
    }
}
