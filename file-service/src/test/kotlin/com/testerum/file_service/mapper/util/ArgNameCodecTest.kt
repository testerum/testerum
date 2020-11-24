package com.testerum.file_service.mapper.util

import com.testerum.file_service.mapper.util.ArgNameCodec.argToVariableName
import com.testerum.file_service.mapper.util.ArgNameCodec.variableToArgName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        assertThat(argToVariableName("a_name"))
            .describedAs("arg -> var")
            .isEqualTo("a_name")
        assertThat(variableToArgName("a_name"))
            .describedAs("var -> arg")
            .isEqualTo("a name")
    }

    @Test
    fun `invalid characters are not preserved when decoded back from var name`() {
        // this test is here to document the limitation
        assertThat(argToVariableName("a?big.name"))
            .describedAs("arg -> var")
            .isEqualTo("a_big_name")
        assertThat(variableToArgName("a_big_name"))
            .describedAs("var -> arg")
            .isEqualTo("a big name")
    }

    @Test
    fun `complex test, that involves all rules, to test that they work correctly together`() {
        assertThat(argToVariableName("1 a long_name  "))
            .describedAs("arg -> var")
            .isEqualTo("_1_a_long_name__")
        assertThat(variableToArgName("_1_a_long_name__"))
            .describedAs("var -> arg")
            .isEqualTo("1 a long name  ")
    }

    private fun testBothDirections(
        argName: String,
        varName: String
    ) {
        assertThat(argToVariableName(argName))
            .describedAs("arg -> var")
            .isEqualTo(varName)
        assertThat(variableToArgName(varName))
            .describedAs("var -> arg")
            .isEqualTo(argName)
    }

}
