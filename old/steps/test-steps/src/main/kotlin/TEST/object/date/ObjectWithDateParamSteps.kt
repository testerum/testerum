package TEST.`object`.date

import TEST.`object`.date.model.*
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator

class ObjectWithDateParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"Date\" param <<Date>> in context with the name <<variableName>>")
    fun objectWithDate(@Param obj: ObjectWithDateParam,
                       @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"Instant\" param <<Instant>> in context with the name <<variableName>>")
    fun objectWithInstant(@Param obj: ObjectWithInstantParam,
                          @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"LocalDate\" param <<LocalDate>> in context with the name <<variableName>>")
    fun objectWithLocalDate(@Param obj: ObjectWithLocalDateParam,
                            @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"LocalDateTime\" param <<LocalDateTime>> in context with the name <<variableName>>")
    fun objectWithLocalDateTime(@Param obj: ObjectWithLocalDateTimeParam,
                                @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"LocalTime\" param <<LocalTime>> in context with the name <<variableName>>")
    fun objectWithLocalTime(@Param obj: ObjectWithLocalTimeParam,
                            @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

    @Given(value = "I add Object with the \"ZonedDateTime\" param <<ZonedDateTime>> in context with the name <<variableName>>")
    fun objectWithZonedDateTime(@Param obj: ObjectWithZonedDateTimeParam,
                                @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

}
