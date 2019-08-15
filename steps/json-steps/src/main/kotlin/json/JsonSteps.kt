package json

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.annotations.steps.Then
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.logger.TesterumLogger
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.expressions.json.JsJson
import com.testerum.model.expressions.json.JsonResource
import json.transformer.JsonTextTransformer
import json_support.module_di.JsonStepsModuleServiceLocator

class JsonSteps {

    private val variables: TestVariables = JsonStepsModuleServiceLocator.bootstrapper.jsonStepsModuleFactory.testVariables
    private val logger: TesterumLogger = TesterumServiceLocator.getTesterumLogger()

    private val jsonComparer: JsonComparer = JsonStepsModuleServiceLocator.bootstrapper.jsonDiffModuleFactory.jsonComparer

    @Given(
            value = "the variable <<name>> with JSON value <<value>>",
            description = "Sets the value of a variable to the given JSON."
    )
    fun declareJsonVariable(name: String,

                            @Param(
                                    transformer = JsonTextTransformer::class
                            )
                            value: JsonResource) {
        val jsonValue = JsJson(value.text)

        val jsonValueForLogging = jsonValue.toPrettyString()
        logger.info(
                "Declaring JSON variable $name:\n" +
                "$jsonValueForLogging\n"
        )

        variables[name] = jsonValue
    }

    @Given(
            value = "the variable <<name>> has the JSON changes <<changes>>",
            description = "Changes a JSON variable."
    )
    fun changeJson(name: String,

                   @Param(
                           transformer = JsonTextTransformer::class
                   )
                   changes: JsonResource) {


        val jsonObject = getJsJsonVariable(name)
        val changesJsJson = JsJson(changes.text)
        val resultObject = jsonObject.overrideWith(changesJsJson)

        val jsonObjectForLogging = jsonObject.toPrettyString()
        val jsonChangesForLogging = changesJsJson.toPrettyString()
        val resultObjectForLogging = resultObject.toPrettyString()

        logger.info(
                "Changing JSON variable $name\n" +
                "\n" +
                "JSON object to change\n" +
                "---------------------\n" +
                "$jsonObjectForLogging\n" +
                "\n" +
                "JSON changes\n" +
                "------------\n" +
                "$jsonChangesForLogging\n" +
                "\n" +
                "Result (after change)\n" +
                "---------------------\n" +
                "$resultObjectForLogging\n" +
                "\n"
        )

        variables[name] = resultObject
    }

    @Then(
            value = "the JSON <<actualValue>> is equal to <<expectedValue>>",
            description = "Compares two JSON values." +
                    " The expected JSON can use comparison modes, and assertion functions, like ``@isNotNull()``.\n" +
                    "See <a target='_blank' href='https://testerum.com/documentation/ui/http/#http-verify-body-json'>JSON verify</a> for details."
    )
    fun compareJsons(@Param(required = false) actualValue: Any?,
                     @Param(required = false) expectedValue: Any?) {
        val actual: String? = getJsonStringForComparison(actualValue, "actualValue")
        val expected: String? = getJsonStringForComparison(expectedValue, "expectedValue")

        if (actual == null) {
            if (expected != null) {
                throw differentJsonException(actual = actual, expected =  expected)
            } else {
                // both are null
                return
            }
        } else {
            if (expected == null) {
                throw differentJsonException(actual = actual, expected =  expected)
            }
        }

        val compareResult = jsonComparer.compare(expectedJson = expected, actualJson = actual)
        if (compareResult is DifferentJsonCompareResult) {
            throw differentJsonException(actual = actual, expected =  expected, compareResult = compareResult)
        }
    }

    private fun differentJsonException(actual: String?,
                                       expected: String?,
                                       compareResult: DifferentJsonCompareResult? = null): AssertionError {
        val prettyActual = actual.prettyPrintJson()
                .lines()
                .joinToString(separator = "\n") {
                    "\t$it"
                }
        val expectedActual = expected.prettyPrintJson()
                .lines()
                .joinToString(separator = "\n") {
                    "\t$it"
                }

        return AssertionError(
                buildString {
                    append("Expected\n")
                    append("$expectedActual\n")
                    append("but found\n")
                    append("$prettyActual\n")

                    if (compareResult != null) {
                        append("Matching message: [${compareResult.message}]\n")
                        append("Not matching element path: [${compareResult.jsonPath}]")
                    }
                }
        )
    }

    private fun getJsJsonVariable(name: String): JsJson {
        if (!variables.contains(name)) {
            throw IllegalArgumentException("the variable with name [$name] does not exist")
        }

        val variableValue = variables[name]
        if (variableValue == null) {
            throw IllegalArgumentException("variable with name [$name] has null value")
        }

        if (variableValue !is JsJson) {
            throw IllegalArgumentException("variable with name [$name] is not of type [${JsJson::class.java.name}], but it's a [${variableValue.javaClass.name}]")
        }

        return variableValue
    }

    private fun getJsonStringForComparison(value: Any?,
                                           name: String): String? {
        if (value == null) {
            return null
        }

        return when (value) {
            is String -> value
            is JsJson -> value.toString()
            else      -> throw IllegalArgumentException("invalid $name type: expected String or ${JsJson::class.simpleName}, but got ${value.javaClass.name}")
        }
    }

}
