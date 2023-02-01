package util

import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.script_executer.ScriptExecuter
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

@Suppress("unused")
class UtilSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val scriptExecuter: ScriptExecuter = TesterumServiceLocator.getScriptExecuter()

    @When(
        value = "I execute the JS script <<script>>",
        description = "Executes a custom JavaScript script that has access to all the variables in the test scope.\n" +
            "This script is not executed in the browser context.\n" +
            "In this script context you have access to the following variables:\n" +
            "- ``testLogger``        - this is a TesterumLogger instance.\n" +
            "- ``testVariables``     - this is a Map with all the variables defined in this test.\n" +
            "- ``failTest(message)`` - function that allows to fail the test with the given message (String).\n"
    )
    fun executeJsScriptStep(
        script: String
    ) {
        logger.info(
            "executing JS script:\n" +
                "--------------------\n" +
                "script : $script\n" +
                "\n"
        )

        executeJsScript(script)
    }

    @When(
        value = "I execute the JS script <<script>> and save the result into the variable <<varName>>",
        description = "Executes a custom JavaScript script that has access to all the variables in the test scope.\n" +
            "The result of the script will be saved into the variable with the given name.\n" +
            "This script is not executed in the browser context.\n" +
            "In this script context you have access to the following variables:\n" +
            "- ``testLogger``        - this is a TesterumLogger instance.\n" +
            "- ``testVariables``     - this is a Map with all the variables defined in this test.\n" +
            "- ``failTest(message)`` - function that allows to fail the test with the given message (String).\n"
    )
    fun executeJsScriptAndSaveToVar(
        script: String,
        varName: String
    ) {
        logger.info(
            "executing JS script and saving result:\n" +
                "--------------------------------------\n" +
                "script  : $script\n" +
                "varName : $varName\n" +
                "\n"
        )

        variables[varName] = executeJsScript(script)
    }

    private fun executeJsScript(script: String): Any? {
        val context = mapOf(
            "testLogger" to logger,
            "testVariables" to variables
        )

        return scriptExecuter.executeScript(script, context)
    }

}
