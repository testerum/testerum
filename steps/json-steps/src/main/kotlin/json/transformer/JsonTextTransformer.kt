package json.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import com.testerum.step_transformer_utils.JsonVariableReplacer
import json.model.JsonResource
import json_support.module_di.JsonStepsModuleServiceLocator
import com.testerum.model.expressions.json.util.JSON_STEPS_OBJECT_MAPPER

class JsonTextTransformer: Transformer<JsonResource> {

    private val jsonVariableReplacer: JsonVariableReplacer = JsonStepsModuleServiceLocator.bootstrapper.jsonStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == JsonResource::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): JsonResource {
        val rootNode: JsonNode = JSON_STEPS_OBJECT_MAPPER.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        val serializedJson: String = JSON_STEPS_OBJECT_MAPPER.writeValueAsString(rootNode)

        return JsonResource(serializedJson)
    }

}
