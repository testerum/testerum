package database.relational.connection_manager

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.testerum.api.test_context.test_vars.TestVariables
import com.testerum.step_transformer_utils.JsonVariableReplacer
import database.relational.connection_manager.model.RdbmsClient
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.model.resources.rdbms.connection.RdbmsConnectionConfig
import net.qutester.service.resources.rdbms.RdbmsService
import net.testerum.db_file.FileRepositoryService
import java.sql.Driver

class RdbmsConnectionManager(private val fileRepositoryService: FileRepositoryService,
                             private val objectMapper: ObjectMapper,
                             private val rdbmsService: RdbmsService,
                             testVariables: TestVariables) {

    private val jsonVariableReplacer = JsonVariableReplacer(testVariables)

    fun getDefaultRdbmsClient(): RdbmsClient? {

        val resourceContexts: List<ResourceContext> = fileRepositoryService
                .getAllResourcesByType(FileType.RDBMS_CONNECTION)
                .map { it.mapToResource() }

        for (resource in resourceContexts) {
            val rootNode: JsonNode = objectMapper.readTree(resource.body)

            jsonVariableReplacer.replaceVariables(rootNode)

            val rdbmsConnectionConfig: RdbmsConnectionConfig = objectMapper.treeToValue(rootNode)

            if (!rdbmsConnectionConfig.isDefaultConnection) {
                return RdbmsClient(
                        rdbmsConnectionConfig,
                        rdbmsService.getDriverInstance(rdbmsConnectionConfig)
                )
            }
        }

        return null
    }

    fun getRdbmsClient(connectionConfig: RdbmsConnectionConfig): RdbmsClient {
        return RdbmsClient(
                connectionConfig,
                rdbmsService.getDriverInstance(connectionConfig)
        )
    }

    fun getDriverInstance(rdbmsConnectionConfig: RdbmsConnectionConfig): Driver {
        return rdbmsService.getDriverInstance(rdbmsConnectionConfig)
    }

}