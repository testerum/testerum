package http.mock.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.model.resources.http.mock.server.HttpMockServer
import com.testerum.step_transformer_utils.JsonVariableReplacer

class HttpMockServerTransformer(val objectMapper: ObjectMapper,
                                testVariables: TestVariables): Transformer<HttpMockServer> {

    private val jsonVariableReplacer = JsonVariableReplacer(testVariables)

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == HttpMockServer::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): HttpMockServer {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        return objectMapper.treeToValue(rootNode)
    }

}