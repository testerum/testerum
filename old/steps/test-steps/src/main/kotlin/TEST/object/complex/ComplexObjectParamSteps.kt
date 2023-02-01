package TEST.`object`.complex

import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import java.util.*

class ComplexParamSteps {

    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "I add the Person <<person>> in context with the name <<variableName>>",
            description = "Add person in the context."
    )
    fun definePerson(@Param(description = "the person parameter") person: Person,
                     @Param(description = "the variable name") variableName: String) {
        testVariables.set(variableName, person)
        logger.info("DEFINED VARIABLE $variableName with value $person")
    }

    @Given(
            value = "I add the Company <<company>> in context with the name <<variableName>>",
            description = "Add company in the context."
    )
    fun defineCompany(@Param(description = "the company parameter") company: Company,
                      @Param(description = "the variable name") variableName: String) {
        testVariables.set(variableName, company)
        logger.info("DEFINED VARIABLE $variableName with value $company")
    }
}


data class Company (
        var name: String?,
        var address: Address?,
        var departmentEmployees: Map<String, Number> = emptyMap(),
        var employees: MutableList<Person> = mutableListOf<Person>()
)

data class Person (
        var name: String?,
        var age: Int?,
        var sex: Sex?,
        var isRightHanded: Boolean?,
        var birthDate: Date?,
        var address: Address?
)

data class Address (
        var street: String?
)

enum class Sex {
    MAN,
    WOMAN,
    UNCLEAR
}
