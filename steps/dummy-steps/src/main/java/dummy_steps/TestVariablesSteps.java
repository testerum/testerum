package dummy_steps;

import com.testerum.api.annotations.steps.Param;
import com.testerum.api.annotations.steps.When;
import com.testerum.api.transformer.ParameterInfo;
import com.testerum.api.transformer.Transformer;
import com.testerum.api.test_context.test_vars.TestVariables;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public final class TestVariablesSteps {

    private static final Random RANDOM = new Random();

    private static final String TEST_VAR = "dynamicallyCreatedVariable";

    private final TestVariables testVariables;

    @Autowired
    public TestVariablesSteps(final TestVariables testVariables) {
        this.testVariables = testVariables;
    }

    @When("I set <<name>> to <<value>>")
    public void set(final String name, final String value) {
        testVariables.set(name, value);
    }

    @When("I set <<name>> to null")
    public void setToNull(final String name) {
        testVariables.set(name, null);
    }

    @When("I create a test variable")
    public void createTestVariable() {
        final int randomNumber = RANDOM.nextInt(100) + 1;

        testVariables.set(TEST_VAR, "test-var-" + randomNumber);
    }

    @When("I print the test variable")
    public void printTestVariable() {
        System.out.println(
                "====================> printing variable: " + testVariables.get(TEST_VAR)
        );
    }

    @When("I call a step to display the list <<list>>")
    public void stepWithACustomTransformer(@Param(transformer = ListOfStringsTransformer.class) final List<String> list) {
        System.out.println(
                "====================> printing list: " + list
        );
    }

    public static class ListOfStringsTransformer implements Transformer<List<String>> {

        private static final Pattern SPLITTER_REGEX = Pattern.compile("\\s*/\\s*");

        @Override
        public boolean canTransform(@NotNull final ParameterInfo paramInfo) {
            return paramInfo.getType() == List.class /* && only list of strings */;
        }

        @Override
        public List<String> transform(@NotNull final String toTransform, @NotNull final ParameterInfo paramInfo) {
            return SPLITTER_REGEX.splitAsStream(toTransform)
                          .filter(Objects::nonNull)
                          .map(String::trim)
                          .filter(item -> !item.isEmpty())
                          .collect(toList());
        }
    }

}
