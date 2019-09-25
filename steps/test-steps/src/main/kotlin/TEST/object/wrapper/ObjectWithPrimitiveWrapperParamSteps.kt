package TEST.`object`.wrapper

import TEST.`object`.wrapper.model.*
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator

class ObjectWithPrimitiveWrapperParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"Boolean\" param <<Boolean>> in context with the name <<variableName>>")
    fun defineBoolean(@Param obj: ObjectWithBooleanWrapperPrimitive,
                      @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Byte\" param <<Byte>> in context with the name <<variableName>>")
    fun defineByte(@Param obj: ObjectWithByteWrapperPrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Character\" param <<Character>> in context with the name <<variableName>>")
    fun defineChar(@Param obj: ObjectWithCharacterWrapperPrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Double\" param <<Double>> in context with the name <<variableName>>")
    fun defineDouble(@Param obj: ObjectWithDoubleWrapperPrimitive,
                     @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Float\" param <<Float>> in context with the name <<variableName>>")
    fun defineFloat(@Param obj: ObjectWithFloatWrapperPrimitive,
                    @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Integer\" param <<Integer>> in context with the name <<variableName>>")
    fun defineInt(@Param obj: ObjectWithIntegerWrapperPrimitive,
                  @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Long\" param <<Long>> in context with the name <<variableName>>")
    fun defineLong(@Param obj: ObjectWithLongWrapperPrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Short\" param <<Short>> in context with the name <<variableName>>")
    fun defineShort(@Param obj: ObjectWithShortWrapperPrimitive,
                    @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }
}
