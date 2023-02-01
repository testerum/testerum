package TEST.date

import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import java.time.*
import java.util.*

class DateParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add the \"Date\" param <<Date>> in context with the name <<variableName>>")
    fun dateParam(@Param date: Date,
                  @Param variableName: String) {
        testVariables[variableName] = date
        logger.info("DEFINED VARIABLE [$variableName] with value [$date]")
    }

    @Given(value = "I add the \"Instant\" param <<Instant>> in context with the name <<variableName>>")
    fun instantParam(@Param instant: Instant,
                     @Param variableName: String) {
        testVariables[variableName] = instant
        logger.info("DEFINED VARIABLE [$variableName] with value [$instant]")
    }

    @Given(value = "I add the \"LocalDate\" param <<LocalDate>> in context with the name <<variableName>>")
    fun localDateParam(@Param localDate: LocalDate,
                       @Param variableName: String) {
        testVariables[variableName] = localDate
        logger.info("DEFINED VARIABLE [$variableName] with value [$localDate]")
    }

    @Given(value = "I add the \"LocalDateTime\" param <<LocalDateTime>> in context with the name <<variableName>>")
    fun localDateTimeParam(@Param localDateTime: LocalDateTime,
                           @Param variableName: String) {
        testVariables[variableName] = localDateTime
        logger.info("DEFINED VARIABLE [$variableName] with value [$localDateTime]")
    }

    @Given(value = "I add the \"LocalTime\" param <<LocalTime>> in context with the name <<variableName>>")
    fun localTimeParam(@Param localTime: LocalTime,
                       @Param variableName: String) {
        testVariables[variableName] = localTime
        logger.info("DEFINED VARIABLE [$variableName] with value [$localTime]")
    }

    @Given(value = "I add the \"ZonedDateTime\" param <<ZonedDateTime>> in context with the name <<variableName>>")
    fun zonedDateTimeParam(@Param zonedDateTime: ZonedDateTime,
                           @Param variableName: String) {
        testVariables[variableName] = zonedDateTime
        logger.info("DEFINED VARIABLE [$variableName] with value [$zonedDateTime]")
    }
}
