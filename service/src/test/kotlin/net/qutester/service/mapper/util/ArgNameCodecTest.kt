package net.qutester.service.mapper.util

import net.qutester.service.mapper.util.ArgNameCodec.argToVariableName
import net.qutester.service.mapper.util.ArgNameCodec.variableToArgName
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test
import org.hamcrest.CoreMatchers.`is` as Is

class ArgNameCodecTest {

    @Test
    fun `empty arg names are transformed to a single underscore`() {
        testBothDirections(argName = "", varName = "_")
    }

    @Test
    fun `when arg name is valid var name, the text should not be transformed`() {
        testBothDirections(argName = "aName", varName = "aName")
    }

    @Test
    fun `spaces in arg name should be replaced with underscore (all lowercase)`() {
        testBothDirections(argName = "a name with spaces", varName = "a_name_with_spaces")
    }

    @Test
    fun `leading and trailing spaces in arg name should be replaced with underscore`() {
        testBothDirections(argName = " a name with leading and trailing spaces   ", varName = "_a_name_with_leading_and_trailing_spaces___")
    }

    @Test
    fun `spaces in arg name should be replaced with underscore (mixed case)`() {
        testBothDirections(argName = " A name WITH SpacEs  ", varName = "_A_name_WITH_SpacEs__")
    }

    @Test
    fun `leading digits in arg name should have an underscore prepended`() {
        testBothDirections(argName = "1name", varName = "_1name")
    }

    @Test
    fun `underscore in arg name is not preserved when decoded back from var name`() {
        // this test is here to document the limitation
        assertThat("arg -> var", argToVariableName("a_name"), Is(equalTo("a_name")))
        assertThat("var -> arg", variableToArgName("a_name"), Is(equalTo("a name")))
    }

    @Test
    fun `invalid characters are not preserved when decoded back from var name`() {
        // this test is here to document the limitation
        assertThat("arg -> var", argToVariableName("a?big.name"), Is(equalTo("a_big_name")))
        assertThat("var -> arg", variableToArgName("a_big_name"), Is(equalTo("a big name")))
    }

    @Test
    fun `complex test, that involves all rules, to test that they work correctly together`() {
        assertThat("arg -> var", argToVariableName("1 a long_name  "), Is(equalTo("_1_a_long_name__")))
        assertThat("var -> arg", variableToArgName("_1_a_long_name__"), Is(equalTo("1 a long name  ")))
    }

    private fun testBothDirections(argName: String,
                                   varName: String) {
        assertThat("arg -> var", argToVariableName(argName), Is(equalTo(varName)))
        assertThat("var -> arg", variableToArgName(varName), Is(equalTo(argName)))
    }

}