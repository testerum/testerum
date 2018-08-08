package http.mock.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.model.resources.http.mock.server.HttpMockServer
import http_support.module_bootstrapper.HttpStepsModuleServiceLocator

class HttpMockServerTransformer: Transformer<HttpMockServer> {

    private val objectMapper: ObjectMapper = HttpStepsModuleServiceLocator.bootstrapper.resourceManagerModuleFactory.resourceJsonObjectMapper
    private val jsonVariableReplacer = HttpStepsModuleServiceLocator.bootstrapper.httpStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == HttpMockServer::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): HttpMockServer {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        return objectMapper.treeToValue(rootNode)
    }

}