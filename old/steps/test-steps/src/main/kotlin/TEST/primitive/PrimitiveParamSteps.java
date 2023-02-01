package TEST.primitive;


import com.testerum_api.testerum_steps_api.annotations.steps.Given;
import com.testerum_api.testerum_steps_api.annotations.steps.Param;
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator;
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger;
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables;

public class PrimitiveParamSteps {

    private TesterumLogger logger = TesterumServiceLocator.getTesterumLogger();
    private TestVariables testVariables = TesterumServiceLocator.getTestVariables();

    @Given(value = "I add the \"byte\" variable <<byte>> in context with the name <<variableName>>")
    public void defineByte(@Param() byte primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"char\" variable <<char>> in context with the name <<variableName>>")
    public void defineChar(@Param() char primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"short\" variable <<short>> in context with the name <<variableName>>")
    public void defineShort(@Param() short primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"int\" variable <<int>> in context with the name <<variableName>>")
    public void defineInt(@Param() int primitive,
                          @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"long\" variable <<long>> in context with the name <<variableName>>")
    public void defineLong(@Param() long primitive,
                           @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"float\" variable <<float>> in context with the name <<variableName>>")
    public void defineFloat(@Param() float primitive,
                            @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"double\" variable <<double>> in context with the name <<variableName>>")
    public void defineDouble(@Param() double primitive,
                             @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }

    @Given(value = "I add the \"boolean\" variable <<boolean>> in context with the name <<variableName>>")
    public void defineBoolean(@Param() boolean primitive,
                              @Param() String variableName) {
        testVariables.set(variableName, primitive);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+primitive+"]");
    }
}
