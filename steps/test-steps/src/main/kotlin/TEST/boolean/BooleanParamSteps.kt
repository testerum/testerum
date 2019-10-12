package TEST.boolean

import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

class BooleanParamSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "I add the \"Boolean\" variable <<Boolean>> in context with the name <<variableName>>",
            description = "Add boolean in the context."
    )
    fun booleanObjParamStep(@Param(description = "the boolean parameter") boolean: Boolean,
                         @Param(description = "the variable name") variableName: String) {
        testVariables.set(variableName, boolean)
        logger.info("DEFINED VARIABLE $variableName with value $boolean")
    }
}
