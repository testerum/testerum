package vars

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables

class VariableSteps {

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given("the variable <<name>> with value <<value>>")
    fun declareVariable(name: String,
                        @Param(required = false) value: String?) {
        variables[name] = value
    }

}
