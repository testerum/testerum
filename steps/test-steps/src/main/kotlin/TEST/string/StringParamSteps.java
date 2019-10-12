package TEST.string;


import com.testerum_api.testerum_steps_api.annotations.steps.Given;
import com.testerum_api.testerum_steps_api.annotations.steps.Param;
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator;
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger;
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables;

public class StringParamSteps {

    private TesterumLogger logger = TesterumServiceLocator.getTesterumLogger();
    private TestVariables testVariables = TesterumServiceLocator.getTestVariables();

    @Given(value = "I add the \"String\" variable <<String>> in context with the name <<variableName>>")
    public void defineByte(@Param() String string,
                           @Param() String variableName) {
        testVariables.set(variableName, string);
        logger.info("DEFINED VARIABLE ["+variableName+"] with value ["+string+"]");
    }
}
