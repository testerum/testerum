package vars

import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

class VariableSteps {

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    @Given(
            value = "the variable <<name>> with value <<value>>",
            description = "Sets the value of a variable."
    )
    fun declareTextVariable(name: String,
                            @Param(required = false) value: Any?) {
        val valueTypeMessage = if (value == null) {
            ""
        } else {
            " (value of type ${value.javaClass.name})"
        }
        logger.info("Declaring variable: $name=$value$valueTypeMessage\n")

        variables[name] = value
    }

}
