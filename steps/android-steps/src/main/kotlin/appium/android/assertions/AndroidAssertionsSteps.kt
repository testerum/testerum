package appium.android.assertions

import appium.AppiumServiceLocator
import appium.android.AndroidStepsUtils
import appium.model.AndroidElementLocatorType
import com.testerum.common_text_matchers.TextMatcherService
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.Then
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import io.appium.java_client.android.AndroidElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait

class AndroidAssertionsSteps {
    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val driver = AndroidStepsUtils.driver

    @Then(
        value = "the text of element identified by <<elementLocatorType>> <<elementLocator>> should be <<textMatchExpression>>",
        description = "" //TODO: implement
    )
    fun startActivity(
        @Param(
            description = "" //TODO: implement
        )
        elementLocatorType: AndroidElementLocatorType,
        @Param(
            description = "" //TODO: implement
        )
        elementLocator: String,
        @Param(
            required = false,
            description = "" //TODO: implement
        )
        textMatchExpression: String,
    ) {
        val waitTimeoutMillis = AppiumServiceLocator.settingsManager.waitTimeoutMillis()

        val searchText = WebDriverWait(driver, waitTimeoutMillis/1000)
            .until(
                ExpectedConditions.visibilityOfElementLocated(
                    AndroidStepsUtils.getElementBy(elementLocatorType, elementLocator)
                )
            ) as AndroidElement

        waitForElement(elementLocatorType, elementLocator) { element ->
            val actualText = element.text
            if (TextMatcherService.doesNotMatch(textMatchExpression, actualText)) {
                throw AssertionError("the text of element [$elementLocator] does not match: expected [$textMatchExpression] but was [$actualText]")
            }
        }
    }

    fun waitForElement(elementLocatorType: AndroidElementLocatorType,elementLocator: String, block: (AndroidElement) -> Unit) {
        val waitTimeoutMillis = AppiumServiceLocator.settingsManager.waitTimeoutMillis()

        val element = WebDriverWait(driver, waitTimeoutMillis/1000)
            .until(
                ExpectedConditions.visibilityOfElementLocated(
                    AndroidStepsUtils.getElementBy(elementLocatorType, elementLocator)
                )
            ) as AndroidElement
        block(element)
    }
}
