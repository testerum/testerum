package selenium_steps_support.service.webdriver_factory.chrome

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.model.selenium.SeleniumDriversByBrowser
import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumDriverSettingValue
import org.openqa.selenium.UnexpectedAlertBehaviour
import org.openqa.selenium.WebDriver
import org.openqa.selenium.remote.CapabilityType
import org.openqa.selenium.safari.SafariDriver
import org.openqa.selenium.safari.SafariOptions
import selenium_steps_support.service.webdriver_factory.WebDriverFactory

object SafariWebDriverFactory : WebDriverFactory {

    override fun createWebDriver(
        config: SeleniumDriverSettingValue,
        webDriverCustomizationScript: String?,
        driversByBrowser: SeleniumDriversByBrowser
    ): WebDriver {
        val options = SafariOptions()

        options.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, UnexpectedAlertBehaviour.IGNORE)

        if (!webDriverCustomizationScript.isNullOrBlank()) {
            ExpressionEvaluator.evaluate(
                expression = webDriverCustomizationScript,
                context = mapOf(
                    "capabilities" to options
                )
            )
        }

        return SafariDriver(options)
    }

}
