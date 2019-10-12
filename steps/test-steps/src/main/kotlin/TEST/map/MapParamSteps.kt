package TEST.map

import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

class MapParamSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "I add the \"Map\" variable <<map>> in context with the name <<variableName>>",
            description = "Add Enum in the context."
    )
    fun mapParamStep(@Param(description = "the Map parameter") map: Map<String, Boolean>,
                     @Param(description = "the variable name") variableName: String) {
        testVariables.set(variableName, map)
        logger.info("DEFINED VARIABLE $variableName with value $map")
    }
}
