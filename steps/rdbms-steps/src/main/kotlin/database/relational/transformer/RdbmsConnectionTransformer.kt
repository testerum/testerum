package database.relational.transformer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer
import database.relational.connection_manager.RdbmsConnectionManager
import database.relational.connection_manager.model.RdbmsConnection
import database.relational.module_di.RdbmsStepsModuleServiceLocator

class RdbmsConnectionTransformer: Transformer<RdbmsConnection> {

    private val rdbmsConnectionManager: RdbmsConnectionManager = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.rdbmsConnectionManager
    private val objectMapper: ObjectMapper = ObjectMapperFactory.RESOURCE_OBJECT_MAPPER
    private val jsonVariableReplacer = RdbmsStepsModuleServiceLocator.bootstrapper.rdbmsStepsModuleFactory.jsonVariableReplacer

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == RdbmsConnection::class.java)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): RdbmsConnection {
        val rootNode: JsonNode = objectMapper.readTree(toTransform)

        jsonVariableReplacer.replaceVariables(rootNode)

        val connectionConfig: RdbmsConnectionConfig = objectMapper.treeToValue(rootNode, RdbmsConnectionConfig::class.java)

        return rdbmsConnectionManager.getRdbmsConnection(connectionConfig)
    }

}
