package database.relational.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum.step_transformer_utils.JsonVariableReplacer
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsClient

class RdbmsConnectionTransformer(private val rdbmsConnectionManager: RdbmsConnectionManager,
                                 private val objectMapper: ObjectMapper,
                                 testVariables: TestVariables): Transformer<RdbmsClient> {

    private val jsonVariableReplacer = JsonVariableReplacer(testVariables)

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == RdbmsClient::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): RdbmsClient {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        val connectionConfig: RdbmsConnectionConfig = objectMapper.treeToValue(rootNode)

        return rdbmsConnectionManager.getRdbmsClient(connectionConfig)
    }

}