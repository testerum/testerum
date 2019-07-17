package selenium.actions.page.popup

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import org.openqa.selenium.NoAlertPresentException
import org.openqa.selenium.WebDriver
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverPopupSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    companion object {
        fun isAlertPresent(driver: WebDriver): Boolean {
            var isPresent: Boolean;
            try {
                driver.switchTo().alert()
                isPresent = true
            } catch (ex: NoAlertPresentException) {
                isPresent = false;
            }
            return isPresent
        }
    }

//======================================================================================================================
    @When(
        value = "I press OK button on the alert, confirm or prompt popup",
        description = "Press OK button on a native browser alert, confirm or prompt popup."
    )
    fun pressOkButtonOnPopup() {
        logger.info(
                "press OK button on the alert, confirm or prompt\n"
        )

        webDriverManager.waitUntil { driver ->
            isAlertPresent(driver);
        }

        webDriverManager.executeWebDriverStep { driver ->
            try {
                driver.switchTo().alert().accept();
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }
        }
    }

//======================================================================================================================
    @When(
        value = "I press Cancel button on the confirm or prompt popup",
        description = "Press Cancel button on a native browser alert, confirm or prompt popup."
    )
    fun pressCancelButtonOnPopup() {
        logger.info(
                "press Cancel button on the alert, confirm or prompt\n"
        )

        webDriverManager.waitUntil { driver ->
            isAlertPresent(driver)
        }

        webDriverManager.executeWebDriverStep { driver ->
            try {
                driver.switchTo().alert().dismiss();
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }
        }
    }

//======================================================================================================================
    @When(
        value = "I set <<text>> in the prompt popup",
        description = "Send the text to the prompt popup"
    )
    fun setTextInPromptPopup(
        @Param(
                required = false,
                description = "The text to set in the prompt popup."
        ) text: String?
    ) {
        logger.info(
                "set text in the prompt popup\n" +
                "-------------------\n" +
                "text : $text\n"
        )

        webDriverManager.waitUntil { driver ->
            isAlertPresent(driver)
        }

        webDriverManager.executeWebDriverStep { driver ->
            try {
                driver.switchTo().alert().sendKeys(text);
            } catch (e: NoAlertPresentException) {
                throw AssertionError("an alert, confirm or prompt should be present on the page, but is not")
            }
        }
    }
}
