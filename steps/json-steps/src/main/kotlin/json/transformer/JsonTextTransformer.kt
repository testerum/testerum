package json.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.step_transformer_utils.JsonVariableReplacer
import json.model.JsonResource
import json_support.module_di.JsonStepsModuleServiceLocator
import json_support.utils.JSON_STEPS_OBJECT_MAPPER

class JsonTextTransformer: Transformer<JsonResource> {

    private val jsonVariableReplacer: JsonVariableReplacer = JsonStepsModuleServiceLocator.bootstrapper.jsonStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == JsonResource::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): JsonResource {
        val rootNode: JsonNode = JSON_STEPS_OBJECT_MAPPER.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        return JSON_STEPS_OBJECT_MAPPER.treeToValue(rootNode)
    }

}
