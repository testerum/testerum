package json

import com.testerum.api.annotations.steps.Given
import com.testerum.api.annotations.steps.Param
import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.test_vars.TestVariables
import json.model.JsJson
import json.model.JsonResource
import json.transformer.JsonTextTransformer

class JsonSteps {

    private val variables: TestVariables = TesterumServiceLocator.getTestVariables()

    @Given(
            value = "the variable <<name>> with JSON value <<value>>",
            description = "Sets the value of a variable to the given JSON.\n" +
                          "If the variable doesn't exist yet, it will be first created."
    )
    fun declareJsonVariable(name: String,

                            @Param(
                                    transformer = JsonTextTransformer::class
                            )
                            value: JsonResource) {
        variables[name] = JsJson(value.text)
    }

    @Given(
            value = "the variable <<name>> has the JSON changes <<overrides>>",
            description = "Changes a JSON variable."
    )
    fun changeJson(name: String,

                   @Param(
                           transformer = JsonTextTransformer::class
                   )
                   overrides: JsonResource) {


        val jsonObject = getJsJsonVariable(name)
        val overridesJsJson = JsJson(overrides.text)

        variables[name] = jsonObject.overrideWith(overridesJsJson)
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
}
