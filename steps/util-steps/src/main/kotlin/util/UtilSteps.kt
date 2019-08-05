package util

import com.testerum.api.annotations.steps.When
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.expression_evaluator.ExpressionEvaluator

class UtilSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    @When(
            value = "I execute the JS script <<script>>",
            description = "Executes a custom JavaScript script that has access to all the variables in the test scope.\n" +
                    "This script is not executed in the browser context.\n" +
                    "In this script context you have access to the following variables:\n" +
                    "- ``testLogger``        - this is a TesterumLogger instance.\n" +
                    "- ``testVariables``     - this is a Map with all the variables defined in this test.\n" +
                    "- ``failTest(message)`` - function that allows to fail the test with the given message (String).\n"
    )
    fun executeJsScript(
            script: String
    ) {
        logger.info(
                "executing JS script:\n" +
                "--------\n" +
                "script : $script\n" +
                "\n"
        )

        val context = hashMapOf<String, Any?>()

        // variables
        context.putAll(variables.toMap())

        // services
        context["testLogger"] = logger
        context["testVariables"] = variables

        ExpressionEvaluator.evaluate(script, context)
    }

}
