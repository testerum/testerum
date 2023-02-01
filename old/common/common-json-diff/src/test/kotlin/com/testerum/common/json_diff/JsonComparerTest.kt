package com.testerum.common.json_diff

import com.testerum.common.json_diff.ExpectedTestOutcome.SHOULD_FAIL
import com.testerum.common.json_diff.ExpectedTestOutcome.SHOULD_SUCCEED
import com.testerum.common.json_diff.impl.compare_mode.JsonCompareMode
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.EqualJsonCompareResult
import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import org.apache.commons.io.IOUtils
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.function.Executable
import java.io.InputStream

class JsonComparerTest {

    companion object {
        private val context = ModuleFactoryContext()
        val comparer: JsonComparer = JsonDiffModuleFactory(context, AssertionFunctionsModuleFactory(context)).jsonComparer

        @AfterAll
        @JvmStatic
        fun tearDown() {
            context.shutdown()
        }
    }

    @Nested
    inner class ExactMode {
        @Test
        fun `should succeed when the only expected field is the same in actual`() {
            performTest("EXACT/should_succeed_when_the_only_expected_field_is_the_same_in_actual", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if comments are present`() {
            performTest("EXACT/should_succeed_if_comments_are_present", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if order of fields is different`() {
            performTest("EXACT/should_succeed_if_order_of_fields_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant decimals are different`() {
            performTest("EXACT/should_succeed_if_insignificant_decimals_are_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant minus is different`() {
            performTest("EXACT/should_succeed_if_insignificant_minus_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if expected field is missing from actual`() {
            performTest("EXACT/should_fail_if_expected_field_is_missing_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual contains extra field not in expected`() {
            performTest("EXACT/should_fail_if_actual_contains_extra_field_not_in_expected", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - boolean`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/boolean", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - text`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/text", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric int`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_int", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric long`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_long", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric bigint`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_bigint", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric decimal`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_decimal", SHOULD_FAIL)
        }

        @Test
        @Disabled("fails because of Jackson bug (https://github.com/FasterXML/jackson-databind/issues/1770); re-enable after the bug is fixed")
        fun `should fail if actual has different value than expected - numeric big decimal`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_bigdecimal", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - object`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/object", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with different values`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/array_different_values", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with missing element from actual`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/array_missing_element_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with extra element in actual`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/array_extra_element_in_actual", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with different element order`() {
            performTest("EXACT/should_fail_if_actual_has_different_value_than_expected/array_different_order", SHOULD_FAIL)
        }

        @Test
        fun `should succeed if complex arrays are equal`() {
            performTest("EXACT/should_succeed_if_complex_arrays_are_equal", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if complex arrays are different`() {
            performTest("EXACT/should_fail_if_complex_arrays_are_different", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual field has different type than expected`() {
            performTestWithDifferentTypes(JsonCompareMode.EXACT)
        }
    }

    @Nested
    inner class UnorderedExactMode {
        @Test
        fun `should succeed when the only expected field is the same in actual`() {
            performTest("UNORDERED_EXACT/should_succeed_when_the_only_expected_field_is_the_same_in_actual", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if comments are present`() {
            performTest("UNORDERED_EXACT/should_succeed_if_comments_are_present", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if order of fields is different`() {
            performTest("UNORDERED_EXACT/should_succeed_if_order_of_fields_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant decimals are different`() {
            performTest("UNORDERED_EXACT/should_succeed_if_insignificant_decimals_are_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant minus is different`() {
            performTest("UNORDERED_EXACT/should_succeed_if_insignificant_minus_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if expected field is missing from actual`() {
            performTest("UNORDERED_EXACT/should_fail_if_expected_field_is_missing_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual contains extra field not in expected`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_contains_extra_field_not_in_expected", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - boolean`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/boolean", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - text`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/text", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric int`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_int", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric long`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_long", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric bigint`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_bigint", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric decimal`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_decimal", SHOULD_FAIL)
        }

        @Test
        @Disabled("fails because of Jackson bug (https://github.com/FasterXML/jackson-databind/issues/1770); re-enable after the bug is fixed")
        fun `should fail if actual has different value than expected - numeric big decimal`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/numeric_bigdecimal", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - object`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/object", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with different values`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/array_different_values", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with missing element from actual`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/array_missing_element_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with extra element in actual`() {
            performTest("UNORDERED_EXACT/should_fail_if_actual_has_different_value_than_expected/array_extra_element_in_actual", SHOULD_FAIL)
        }

        @Test
        fun `should succeed if actual array has different order`() {
            performTest("UNORDERED_EXACT/should_succeed_if_actual_array_has_different_order", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if complex arrays are equal`() {
            performTest("UNORDERED_EXACT/should_succeed_if_complex_arrays_are_equal", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if complex arrays are equal ignoring order`() {
            performTest("UNORDERED_EXACT/should_succeed_if_complex_arrays_are_equal_ignoring_order", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if complex arrays are different`() {
            performTest("UNORDERED_EXACT/should_fail_if_complex_arrays_are_different", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual field has different type than expected`() {
            performTestWithDifferentTypes(JsonCompareMode.UNORDERED_EXACT)
        }
    }

    @Nested
    inner class ContainsMode {
        @Test
        fun `should succeed when the only expected field is the same in actual`() {
            performTest("CONTAINS/should_succeed_when_the_only_expected_field_is_the_same_in_actual", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if comments are present`() {
            performTest("CONTAINS/should_succeed_if_comments_are_present", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if order of fields is different`() {
            performTest("CONTAINS/should_succeed_if_order_of_fields_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant decimals are different`() {
            performTest("CONTAINS/should_succeed_if_insignificant_decimals_are_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if insignificant minus is different`() {
            performTest("CONTAINS/should_succeed_if_insignificant_minus_is_different", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if expected field is missing from actual`() {
            performTest("CONTAINS/should_fail_if_expected_field_is_missing_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should succeed if actual contains extra field not in expected`() {
            performTest("CONTAINS/should_succeed_if_actual_contains_extra_field_not_in_expected", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if actual has different value than expected - boolean`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/boolean", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - text`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/text", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric int`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/numeric_int", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric long`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/numeric_long", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric bigint`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/numeric_bigint", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - numeric decimal`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/numeric_decimal", SHOULD_FAIL)
        }

        @Test
        @Disabled("fails because of Jackson bug (https://github.com/FasterXML/jackson-databind/issues/1770); re-enable after the bug is fixed")
        fun `should fail if actual has different value than expected - numeric big decimal`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/numeric_bigdecimal", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - object`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/object", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with different values`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/array_different_values", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual has different value than expected - array with missing element from actual`() {
            performTest("CONTAINS/should_fail_if_actual_has_different_value_than_expected/array_missing_element_from_actual", SHOULD_FAIL)
        }

        @Test
        fun `should succeed if actual array has extra element`() {
            performTest("CONTAINS/should_succeed_if_actual_array_has_extra_element", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if actual array has different order`() {
            performTest("CONTAINS/should_succeed_if_actual_array_has_different_order", SHOULD_SUCCEED)
        }

        @Test
        fun `should succeed if complex arrays are equal`() {
            performTest("CONTAINS/should_succeed_if_complex_arrays_are_equal", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if complex arrays are different`() {
            performTest("CONTAINS/should_fail_if_complex_arrays_are_different", SHOULD_FAIL)
        }

        @Test
        fun `should fail if actual field has different type than expected`() {
            performTestWithDifferentTypes(JsonCompareMode.CONTAINS)
        }
    }

    @Nested
    inner class OtherTests {
        @Test
        fun `should succeed if the array compare mode is contains and the expected array has less elements than the actual`() {
            performTest("array_incorrect_compare_mode", SHOULD_SUCCEED)
        }

        @Test
        fun `should fail if the array compare mode is exact and the expected array has less elements than the actual`() {
            val testFilesDirectory = "array_incorrect_compare_mode_2"
            val expectedJson: String = loadClasspathResource("$testFilesDirectory/expected.json")
            val actualJson: String = loadClasspathResource("$testFilesDirectory/actual.json")

            val compareResult: JsonCompareResult = comparer.compare(expectedJson, actualJson)
            assertThat(compareResult).isInstanceOf(DifferentJsonCompareResult::class.java)

            val differentJsonCompareResult = compareResult as DifferentJsonCompareResult
            assertThat(differentJsonCompareResult.message)
                .isEqualTo("mismatched number of items in the arrays: expected 1, but got 2 instead")
            assertThat(differentJsonCompareResult.jsonPath.toString())
                .isEqualTo("\$.members")
        }
    }

    private fun performTestWithDifferentTypes(compareMode: JsonCompareMode) {
        // can't use local enum...
        val NULL = "NULL"
        val BOOLEAN = "BOOLEAN"
        val NUMERIC = "NUMERIC"
        val TEXT = "TEXT"
        val ARRAY = "ARRAY"
        val OBJECT = "OBJECT"
        val fieldTypes = setOf(NULL, BOOLEAN, NUMERIC, TEXT, ARRAY, OBJECT)

        val valuesForType = mapOf(
            NULL to "null",
            BOOLEAN to "false",
            NUMERIC to "0",
            TEXT to "\"\"",
            ARRAY to "[]",
            OBJECT to "{}"
        )

        val assertions = mutableListOf<Executable>()

        for (expectedType in fieldTypes) {
            for (actualType in fieldTypes) {
                if (actualType == expectedType) {
                    continue
                }

                val expectedJson = """
                        |{
                        |   "=compareMode": "${compareMode.code}",
                        |   "field": ${valuesForType[expectedType]}
                        |}
                        """.trimMargin()
                val actualJson = """
                        |{
                        |   "field": ${valuesForType[actualType]}
                        |}
                        """.trimMargin()

                assertions.add(Executable {
                    val compareResult: JsonCompareResult = comparer.compare(expectedJson, actualJson)

                    if (compareResult is EqualJsonCompareResult) {
                        throw AssertionError("comparing expectedType=[$expectedType], actualType=[$actualType] should have failed")
                    }
                })
            }
        }

        assertAll(assertions.stream())
    }

    private fun performTest(testFilesDirectory: String, expectedTestOutcome: ExpectedTestOutcome) {
        val expectedJson: String = loadClasspathResource("$testFilesDirectory/expected.json")
        val actualJson: String = loadClasspathResource("$testFilesDirectory/actual.json")

        val compareResult: JsonCompareResult = comparer.compare(expectedJson, actualJson)
        when (expectedTestOutcome) {
            SHOULD_SUCCEED -> {
                if (compareResult is DifferentJsonCompareResult) {
                    throw AssertionError(
                        "expected the test to succeed" +
                            ", but got instead a failure" +
                            " with message [${compareResult.message}]" +
                            ", path=[${compareResult.jsonPath}]"
                    )
                }
            }
            SHOULD_FAIL -> {
                if (compareResult is EqualJsonCompareResult) {
                    throw AssertionError(
                        "expected the test to fail, but got instead a success"
                    )
                }
            }
        }
    }

    private fun loadClasspathResource(fullName: String): String {
        val classLoader = Thread.currentThread().contextClassLoader

        val resourceStream: InputStream = classLoader.getResourceAsStream(fullName)
            ?: throw IllegalArgumentException("cannot find resource [$fullName] in the classpath")

        return resourceStream.use { IOUtils.toString(it, Charsets.UTF_8) }
    }

}
