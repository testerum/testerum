package selenium.actions.util

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.script_executer.ScriptExecuter
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import org.openqa.selenium.JavascriptExecutor
import selenium_steps_support.service.module_di.SeleniumModuleServiceLocator
import selenium_steps_support.service.webdriver_manager.WebDriverManager

class WebDriverExpressionSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()
    private val scriptExecuter: ScriptExecuter = TesterumServiceLocator.getScriptExecuter()
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
    fun executeWebDriverJsScript(script: String) {
        logger.info(
                "executing JS WebDriver script:\n" +
                "--------\n" +
                "script : $script\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val context = mapOf<String, Any?>(
                "webDriver" to driver
            )

            scriptExecuter.executeScript(script, context)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I execute the WebDriver JS script <<script>> in the browser",
            description = "Executes JavaScript in the context of the currently selected frame or window. " +
                    "The script fragment provided will be executed as the body of an anonymous function."
    )
    fun evaluateJavaScriptExpresion(
            script: String
    ) {
        logger.info(
                "executing JavaScript script:\n" +
                        "----------------------------\n" +
                        "script : $script\n" +
                        "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            (driver as JavascriptExecutor).executeScript(script)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @When(
            value = "I execute the WebDriver JS script <<script>> in the browser and save the return into the variable <<varName>>",
            description = "Executes JavaScript in the context of the currently selected frame or window.\n" +
                    "The script fragment provided will be executed as the body of an anonymous function.\n" +
                    "After execution, the value returned by this script will be stored in the specified variable."
    )
    fun saveValueFromJsIntoVariable(
            script: String,

            @Param(
                    required = false,
                    description = "The name of the variable that will store the value."
            ) varName: String
    ) {
        logger.info(
                "executing JavaScript script which returns the value in the variable:\n" +
                        "--------------------------------------------------------------------\n" +
                        "script : $script\n" +
                        "varName: $varName\n" +
                        "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val result = (driver as JavascriptExecutor).executeScript(script)
            variables.set(varName, result)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @When(
        value = "I execute async the WebDriver JS script <<script>> in the browser",
        description = "Schedules a command to execute asynchronous JavaScript in the context of the currently selected frame or window.\n" +
            "The script fragment will be executed as the body of an anonymous function.\n" +
            "\n" +
            "For more details on how to use this step please see the WebDriver ``executeAsyncScript`` function documentation"
    )
    fun evaluateJavaScriptExpresionAsync(
        script: String
    ) {
        logger.info(
            "executing JavaScript script:\n" +
                "----------------------------\n" +
                "script : $script\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            (driver as JavascriptExecutor).executeAsyncScript(script)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    @When(
        value = "I execute async the WebDriver JS script <<script>> in the browser and save the return into the variable <<varName>>",
        description = "Schedules a command to execute asynchronous JavaScript in the context of the currently selected frame or window.\n" +
            "The script fragment will be executed as the body of an anonymous function.\n" +
            "After execution, the value returned by this script will be stored in the specified variable.\n" +
            "\n" +
            "For more details on how to use this step please see the WebDriver ``executeAsyncScript`` function documentation"
    )
    fun evaluateJavaScriptExpresionAsyncAndSaveReturn(
        script: String,

        @Param(
            required = false,
            description = "The name of the variable that will store the value."
        ) varName: String
    ) {
        logger.info(
            "executing JavaScript script:\n" +
                "----------------------------\n" +
                "script : $script\n" +
                "\n"
        )

        webDriverManager.executeWebDriverStep { driver ->
            val result = (driver as JavascriptExecutor).executeAsyncScript(script)
            variables.set(varName, result)
        }
    }
}
