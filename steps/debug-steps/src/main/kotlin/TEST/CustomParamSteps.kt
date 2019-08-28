package TEST

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables

class CustomParamSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "I LOG <<person>>",
            description = "Log the Person parameter."
    )
    fun waitForAPeriodOfTime(@Param(description = "the person parameter") person: Person) {
        logger.error("PERSON: $person")
    }
}