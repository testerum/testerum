package http.response.verify.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.step_transformer_utils.JsonVariableReplacer
import http.response.verify.model.HttpResponseVerify

class HttpResponseVerifyTransformer(private val objectMapper: ObjectMapper,
                                    testVariables: TestVariables): Transformer<HttpResponseVerify> {

    private val jsonVariableReplacer = JsonVariableReplacer(testVariables)

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == HttpResponseVerify::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): HttpResponseVerify {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        return objectMapper.treeToValue(rootNode)
    }

}