package TEST.wrapper;

import com.testerum.api.annotations.steps.Given;
import com.testerum.api.annotations.steps.Param;
import com.testerum.api.services.TesterumServiceLocator;
import com.testerum.api.test_context.logger.TesterumLogger;
import com.testerum.api.test_context.test_vars.TestVariables;

public class WrapperParamSteps {

    private TesterumLogger logger = TesterumServiceLocator.getTesterumLogger();
    private TestVariables testVariables = TesterumServiceLocator.getTestVariables();

    @Given(value = "I add the \"Byte\" variable <<Byte>> in context with the name <<variableName>>")
    public void defineByte(@Param() Byte primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Character\" variable <<Character>> in context with the name <<variableName>>")
    public void defineChar(@Param() Character primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Short\" variable <<Short>> in context with the name <<variableName>>")
    public void defineShort(@Param() Short primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Integer\" variable <<Integer>> in context with the name <<variableName>>")
    public void defineInt(@Param() Integer primitive,
                          @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Long\" variable <<Long>> in context with the name <<variableName>>")
    public void defineLong(@Param() Long primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Float\" variable <<Float>> in context with the name <<variableName>>")
    public void defineFloat(@Param() Float primitive,
                            @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Double\" variable <<Double>> in context with the name <<variableName>>")
    public void defineDouble(@Param() Double primitive,
                             @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"Boolean\" variable <<Boolean>> in context with the name <<variableName>>")
    public void defineBoolean(@Param() Boolean primitive,
                              @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }
}
