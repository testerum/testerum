package TEST.`object`.primitive

import TEST.`object`.primitive.model.*
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator

class ObjectWithPrimitiveParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"boolean\" param <<boolean>> in context with the name <<variableName>>")
    fun defineBoolean(@Param obj: ObjectWithBooleanPrimitive,
                      @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"byte\" param <<byte>> in context with the name <<variableName>>")
    fun defineByte(@Param obj: ObjectWithBytePrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"char\" param <<char>> in context with the name <<variableName>>")
    fun defineChar(@Param obj: ObjectWithCharPrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"double\" param <<double>> in context with the name <<variableName>>")
    fun defineDouble(@Param obj: ObjectWithDoublePrimitive,
                     @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"float\" param <<float>> in context with the name <<variableName>>")
    fun defineFloat(@Param obj: ObjectWithFloatPrimitive,
                    @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"int\" param <<int>> in context with the name <<variableName>>")
    fun defineInt(@Param obj: ObjectWithIntPrimitive,
                  @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"long\" param <<long>> in context with the name <<variableName>>")
    fun defineLong(@Param obj: ObjectWithLongPrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"short\" param <<short>> in context with the name <<variableName>>")
    fun defineShort(@Param obj: ObjectWithShortPrimitive,
                    @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

}
