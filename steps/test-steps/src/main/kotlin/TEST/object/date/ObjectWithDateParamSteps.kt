package TEST.`object`.date

import TEST.`object`.date.model.ObjectWithDateParam
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator

class ObjectWithDateParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"Date\" param <<Date>> in context with the name <<variableName>>")
    fun defineBoolean(@Param obj: ObjectWithDateParam,
                      @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

}
