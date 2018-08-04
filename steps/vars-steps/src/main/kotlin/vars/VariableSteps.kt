package vars

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.test_context.test_vars.TestVariables
import org.springframework.beans.factory.annotation.Autowired

class VariableSteps @Autowired constructor(private val variables: TestVariables) {

    @Given("the variable <<name>> with value <<value>>")
    fun declareVariable(name: String,
                        @Param(required = false) value: String?) {
        variables[name] = value
    }

}