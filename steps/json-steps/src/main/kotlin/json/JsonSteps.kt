package json

import com.testerum.common.json_diff.JsonComparer
import com.testerum.common.json_diff.impl.node_comparer.DifferentJsonCompareResult
import com.testerum.common_json.util.prettyPrintJson
import com.testerum.model.expressions.json.JsJson
import com.testerum_api.testerum_steps_api.annotations.steps.Given
import com.testerum_api.testerum_steps_api.annotations.steps.Param
import com.testerum_api.testerum_steps_api.annotations.steps.Then
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.logger.TesterumLogger
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables
import json.model.JsonResource
import json.model.JsonVerifyResource
import json.transformer.JsonResourceTransformer
import json.transformer.JsonVerifyResourceTransformer
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
                            @Param(transformer = JsonResourceTransformer::class) value: JsonResource) {
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
                   @Param(transformer = JsonResourceTransformer::class) changes: JsonResource) {


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
            value = "the variable <<varName>> is equals to JSON <<expectedJSON>>",
            description = "Changes a JSON variable."
    )
    fun compareVariableToJson(varName: String,
                              @Param(transformer = JsonVerifyResourceTransformer::class) expectedJSON: JsonVerifyResource?) {
        val actual: String? = getJsonStringFromVariable(varName)
        val expected: String? = expectedJSON?.text

        compareJsonsStrings(actual, expected)
    }


    @Then(
        value = "the JSON <<actualValue>> is equal to <<expectedValue>>",
        description = "Compares two JSON values." +
                " The expected JSON can use comparison modes, and assertion functions, like ``@isNotNull()``.\n" +
                "See <a target='_blank' href='https://testerum.com/documentation/ui/http/#http-verify-body-json'>JSON verify</a> for details."
    )
    fun compareJsons(@Param(transformer = JsonVerifyResourceTransformer::class, required = false) actualValue: JsonVerifyResource?,
                     @Param(transformer = JsonVerifyResourceTransformer::class, required = false) expectedValue: JsonVerifyResource?) {
        val actual: String? = actualValue?.text
        val expected: String? = expectedValue?.text

        compareJsonsStrings(actual, expected)
    }

    private fun compareJsonsStrings(actual: String?, expected: String?) {
        if (actual == null) {
            if (expected != null) {
                throw differentJsonException(actual = actual, expected = expected)
            } else {
                // both are null
                return
            }
        } else {
            if (expected == null) {
                throw differentJsonException(actual = actual, expected = expected)
            }
        }

        val compareResult = jsonComparer.compare(expectedJson = expected, actualJson = actual)
        if (compareResult is DifferentJsonCompareResult) {
            throw differentJsonException(actual = actual, expected = expected, compareResult = compareResult)
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

    private fun getJsonStringFromVariable(variableName: String): String? {
        if (!variables.contains(variableName)) {
            throw IllegalArgumentException("the variable with name [$variableName] does not exist")
        }

        val variableValue = variables[variableName] ?: return null

        return when (variableValue) {
            is String       -> variableValue
            is JsJson       -> variableValue.toString()
            is JsonResource -> variableValue.text
            is JsonVerifyResource -> variableValue.text
            else            -> throw IllegalArgumentException("variable with name [$variableName] is not of type [${JsonResource::class.java.name}], but it's a [${variableValue.javaClass.name}]")
        }
    }
}
