package selenium.assertions.page.popup

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.NoAlertPresentException
import selenium.actions.page.popup.WebDriverPopupSteps
import selenium_steps_support.service.descriptions.SeleniumSharedDescriptions
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.text_match.TextMatcherService
import selenium_steps_support.service.webdriver_manager.WebDriverManager


class WebDriverPopupAssertions {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @Then(
            value = "an alert, confirm or prompt is present",
            description = "Checks if an alert, confirm or prompt popup is showed."
    )
    fun assertAlertIsPresent() {
        logger.info(
                "an alert is present"
        )

        webDriverManager.waitUntil { driver ->
            WebDriverPopupSteps.isAlertPresent(driver)
        }

        webDriverManager.executeWebDriverStep { driver ->
            try {
                driver.switchTo().alert();
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }
        }
    }

//======================================================================================================================
    @Then(
            value = "an alert, confirm or prompt is not present",
            description = "Checks that native browser alert, confirm or prompt popup is not present on the page."
    )
    fun assertAlertIsNotPresent() {
        logger.info(
            "Check if an alert, confirm or prompt is not present"
        )

        webDriverManager.waitUntil { driver ->
            !WebDriverPopupSteps.isAlertPresent(driver)
        }

        webDriverManager.executeWebDriverStep { driver ->
            try {
                driver.switchTo().alert();
            } catch (e: NoAlertPresentException) {
                return@executeWebDriverStep;
            }
            throw AssertionError("an alert, confirm or prompt should not be present on the page, but it is")
        }
    }

//======================================================================================================================
    @Then(
            value = "the alert, confirm or prompt message is <<alertMessage>>",
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

        webDriverManager.waitUntil { driver ->
            WebDriverPopupSteps.isAlertPresent(driver)
        }

        webDriverManager.executeWebDriverStep { driver ->
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
