package debug

import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.When
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import org.apache.commons.lang3.StringUtils

class DebugSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @When(
        value = "I wait <<waitDurationMillis>> milliseconds",
        description = "Pauses the test execution for the specified duration."
    )
    fun waitForAPeriodOfTime(@Param(description = "the duration of the time to wait, in milliseconds") waitDurationMillis: Long) {
        Thread.sleep(waitDurationMillis)
    }

    @When(
        value = "I log all the variables",
        description = "Shows all the variables and their values in the log. All variables that can be used at this point will be logged."
    )
    fun logVariables() {
        logger.info(
            "Variables\n" +
            "---------\n" +
            testVariables.getVariablesDetailsForDebugging()
        )
    }

}
