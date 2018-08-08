package database.relational.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsClient
import database.relational.module_bootstrapper.RdbmsStepsModuleServiceLocator

class RdbmsConnectionTransformer: Transformer<RdbmsClient> {

    private val rdbmsConnectionManager: RdbmsConnectionManager = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.rdbmsConnectionManager
    private val objectMapper: ObjectMapper = RdbmsStepsModuleServiceLocator.bootstrapper.resourceManagerModuleFactory.resourceJsonObjectMapper
    private val jsonVariableReplacer = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == RdbmsClient::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): RdbmsClient {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        val connectionConfig: RdbmsConnectionConfig = objectMapper.treeToValue(rootNode)

        return rdbmsConnectionManager.getRdbmsClient(connectionConfig)
    }

}