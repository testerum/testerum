package TEST.`object`.primitive

import TEST.`object`.primitive.model.ObjectWithBytePrimitive
import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator

class ObjectWithPrimitiveParamSteps {

    private val logger = TesterumServiceLocator.getTesterumLogger()
    private val testVariables = TesterumServiceLocator.getTestVariables()

    @Given(value = "I add Object with the \"byte\" param <<byte>> in context with the name <<variableName>>")
    fun defineByte(@Param obj: ObjectWithBytePrimitive,
                   @Param variableName: String) {
        testVariables[variableName] = obj
        logger.info("DEFINED VARIABLE [$variableName] with value [$obj]")
    }

}
