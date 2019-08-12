package selenium.actions.util

import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.expression_evaluator.ExpressionEvaluator
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverExpressionSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I execute the WebDriver JS script <<script>>",
            description = "Executes a custom JavaScript script that has access to the WebDriver and all the variables in the test scope.\n" +
                    "This script is not executed in the browser context.\n" +
                    "In this script context you have access to the following variables:\n" +
                    "- ``testLogger``    - this is a TesterumLogger instance.\n" +
                    "- ``testVariables`` - this is a Map with all the variables defined in this test.\n" +
                    "- ``failTest(message)`` - function that allows to fail the test with the given message (String).\n" +
                    "- ``webDriver``     - this is a WebDriver instance and allows you to execute any Selenium action you want (e.g. ``webDriver.findElement(By.id('submit').click();``).\n"
    )
    fun executeWebDriverJsScript(
            script: String
    ) {
        logger.info(
                "executing JS WebDriver script:\n" +
                "--------\n" +
                "script : $script\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val context = hashMapOf<String, Any?>()

            // variables
            context.putAll(variables.toMap())

            // services
            context["testLogger"] = logger
            context["testVariables"] = variables

            // misc
            context["webDriver"] = driver

            ExpressionEvaluator.evaluate(script, context)
        }
    }

}
