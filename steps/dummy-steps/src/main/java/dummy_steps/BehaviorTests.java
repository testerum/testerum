package dummy_steps;

import com.testerum.api.annotations.steps.Given;
import com.testerum.api.annotations.steps.Then;
import com.testerum.api.annotations.steps.When;

import static org.assertj.core.api.Assertions.assertThat;

public class BehaviorTests {

    @Given("the test without exceptions and run time of <<seconds>> seconds")
    public void testGivenNormal(final int seconds) {
        sleep(seconds);
    }

    @Given("the test with exceptions and run time of <<seconds>> seconds")
    public void testGivenException(final int seconds) {
        sleep(seconds);
        throw new RuntimeException("Expected exception");
    }

    @When("test without exceptions and run time of <<seconds>> seconds")
    public void testWhenNormal(final int seconds) {
        sleep(seconds);
    }

    @When("test with exceptions and run time of <<seconds>> seconds")
    public void testWhenException(final int seconds) {
        sleep(seconds);
        throw new RuntimeException("Expected exception");
    }

    @Then("the test is failing and run time of <<seconds>> seconds")
    public void testFailing(final int seconds) {
        sleep(seconds);
        assertThat(true).isFalse();
    }

    @Then("the test is passing and run time of <<seconds>> seconds")
    public void testPassing(final int seconds) {
        sleep(seconds);
        assertThat(true).isTrue();
    }

    private void sleep(final int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (final InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
