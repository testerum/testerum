package TEST.`object`.list

import TEST.`object`.list.model.ObjectWithListOfDatesParam
import TEST.`object`.list.model.ObjectWithListOfObjectsParam
import TEST.`object`.list.model.ObjectWithListOfStringsParam
import TEST.`object`.list.model.ObjectWithSetOfStringsParam
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator

class ObjectWithListParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"List<String>\" param <<List>> in context with the name <<variableName>>")
    fun objectWithListOfStrings(@Param obj: ObjectWithListOfStringsParam,
                       @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"List<Object>\" param <<List>> in context with the name <<variableName>>")
    fun objectWithListOfObject(@Param obj: ObjectWithListOfObjectsParam,
                               @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"List<Date>\" param <<List>> in context with the name <<variableName>>")
    fun objectWithListOfDates(@Param obj: ObjectWithListOfDatesParam,
                              @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }


    @Given(value = "I add Object with the \"Set<String>\" param <<Set>> in context with the name <<variableName>>")
    fun objectWithSetOfStrings(@Param obj: ObjectWithSetOfStringsParam,
                               @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }
}