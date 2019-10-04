package assertions

import assertions.util.toIsoString
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import java.util.*

class AssertionsSteps {

    @Then("<<actualValue>> is equal to <<expectedValue>>")
    fun assertEqualValues(@Param(required = false) actualValue: Any?,
                          @Param(required = false) expectedValue: Any?) {
        var actual: Any? = actualValue
        var expected: Any? = expectedValue

        if (actualValue is String && expectedValue !is String) {
            expected = when (expectedValue) {
                is Date -> expectedValue.toIsoString();
                else -> expectedValue.toString()
            }
        }

        if (expectedValue is String && actualValue !is String) {
            actual = when (actualValue) {
                is Date -> actualValue.toIsoString();
                else -> actualValue.toString()
            }
        }

        if (actual != expected) {
            val errorMessage = buildString {
                append("expected ")
                append(" actualValue=[").append(actual).append("]")
                if (actual !== actualValue) {
                    append(" (from toString())")
                }
                append(" to be equal to ")
                append(" expectedValue=[").append(expected).append("]")
                if (expected !== expectedValue) {
                    append(" (from toString())")
                }
                append(", but they are different")
            }

            throw AssertionError(errorMessage)
        }
    }

    @Then("<<actualValue>> is not equal to <<expectedValue>>")
    fun assertDifferentValues(@Param(required = false) actualValue: Any?,
                              @Param(required = false) expectedValue: Any?) {
        var actual: Any? = actualValue
        var expected: Any? = expectedValue

        if (actualValue is String && expectedValue !is String) {
            if (expectedValue is Date) {
                expected = expectedValue.toIsoString();
            } else{
                expected = expectedValue.toString()
            }
        }

        if (expectedValue is String && actualValue !is String) {
            if (actualValue is Date) {
                actual = actualValue.toIsoString();
            } else{
                actual = actualValue.toString()
            }
        }

        if (actual == expected) {
            val errorMessage = buildString {
                append("expected ")
                append(" actualValue=[").append(actual).append("]")
                if (actual !== actualValue) {
                    append(" (from toString())")
                }
                append(" to be different from ")
                append(" expectedValue=[").append(expected).append("]")
                if (expected !== expectedValue) {
                    append(" (from toString())")
                }
                append(", but they are equal")
            }

            throw AssertionError(errorMessage)
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
