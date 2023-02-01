package TEST.`object`.map

import TEST.`object`.map.model.ObjectWithMapOfStringAndDateParam
import TEST.`object`.map.model.ObjectWithMapOfStringAndObjectParam
import TEST.`object`.map.model.ObjectWithMapOfStringAndStringParam
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator

class ObjectWithMapParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"Map<String, Date>\" param <<mapOfStringAndDate>> in context with the name <<variableName>>")
    fun objectWithMapOfStringAndDate(@Param obj: ObjectWithMapOfStringAndDateParam,
                                     @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Map<String, Object>\" param <<mapOfStringAndObject>> in context with the name <<variableName>>")
    fun objectWithMapOfStringAndObject(@Param obj: ObjectWithMapOfStringAndObjectParam,
                                       @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Map<String, String>\" param <<mapOfStringAndString>> in context with the name <<variableName>>")
    fun objectWithMapOfStringAndString(@Param obj: ObjectWithMapOfStringAndStringParam,
                                       @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }
}
