package assertions

import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then

class AssertionsSteps {

    @Then("<<actualValue>> is equal to <<expectedValue>>")
    fun assertEqualValues(@Param(required = false) actualValue: Any?,
                          @Param(required = false) expectedValue: Any?) {
        if (actualValue != expectedValue) {
            throw AssertionError("expected [$actualValue] to be equal to [$expectedValue], but was not")
        }
    }

    @Then("<<actualValue>> is not equal to <<expectedValue>>")
    fun assertDifferentValues(@Param(required = false) actualValue: Any?,
                              @Param(required = false) expectedValue: Any?) {
        if (actualValue == expectedValue) {
            throw AssertionError("expected [$actualValue] to different from [$expectedValue], but they are equal")
        }
    }

    @Then("<<actualValue>> is true")
    fun assertTrueValue(@Param(required = false) actualValue: Any?) {
        assertEqualValues(actualValue, true)
    }

    @Then("<<actualValue>> is false")
    fun assertFalseValue(@Param(required = false) actualValue: Any?) {
        assertEqualValues(actualValue, false)
    }

    @Then("<<actualValue>> is null")
    fun assertNullValue(@Param(required = false) actualValue: Any?) {
        assertEqualValues(actualValue, null)
    }

    @Then("<<actualValue>> is not null")
    fun assertNotNullValue(@Param(required = false) actualValue: Any?) {
        assertDifferentValues(actualValue, null)
    }

    // todo: add more; get ideas from http://hamcrest.org/JavaHamcrest/javadoc/2.0.0.0/

}