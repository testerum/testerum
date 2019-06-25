package selenium.actions.util

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.expression_evaluator.helpers.util.JsFunction
import com.testerum.common.expression_evaluator.helpers.util.ScriptingArgs
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverExpressionSteps {


    companion object {
        private val failTestFunction = object : JsFunction(functionName = "failTest") {
            override fun call(thiz: Any?, args: ScriptingArgs): Any? {
                args.requireLength(1)
                val errorMessage: String = args[0]

                throw AssertionError(errorMessage)
            }
        }
    }

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val webDriverManager: WebDriverManager = SeleniumModuleServiceLocator.bootstrapper.seleniumModuleFactory.webDriverManager

    @When(
            value = "I execute the WebDriver JS script <<script>>",
            description = ""
    )
    fun evaluateWebDriverExpresion(
            @Param(
                    description = ""
            )
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
            context["testVariables"] = variables

            // misc
            context["webDriver"] = driver
            context[failTestFunction.functionName] = failTestFunction

            ExpressionEvaluator.evaluate(script, context)
        }
    }
}
