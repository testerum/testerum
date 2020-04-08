package http.response.verify.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_json.ObjectMapperFactory
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import http.response.verify.model.HttpResponseVerify
import http_support.module_di.HttpStepsModuleServiceLocator

class HttpResponseVerifyTransformer: Transformer<HttpResponseVerify> {

    private val objectMapper: ObjectMapper = ObjectMapperFactory.RESOURCE_OBJECT_MAPPER
    private val jsonVariableReplacer = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == HttpResponseVerify::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): HttpResponseVerify {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        return objectMapper.treeToValue(rootNode, HttpResponseVerify::class.java)
    }

}
