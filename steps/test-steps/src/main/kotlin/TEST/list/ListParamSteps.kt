package TEST.list

import TEST.enum.CarsEnum
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

class ListParamSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "I add the \"List\" variable <<cars>> in context with the name <<variableName>>",
            description = "Add Enum in the context."
    )
    fun listObjParamStep(@Param(description = "the List parameter") cars: List<CarsEnum>,
                         @Param(description = "the variable name") variableName: String) {
        testVariables.set(variableName, cars)
        logger.info("DEFINED VARIABLE $variableName with value $cars")
    }
}
