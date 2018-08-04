package net.qutester.service.resources.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.model.resources.rdbms.connection.RdbmsConnectionConfig
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange

class RdbmsConnectionConfigHandler(val objectMapper: ObjectMapper,
                                   val fileRepositoryService: FileRepositoryService): ResourceHandler {
    override fun handle(resourceContext: ResourceContext): ResourceContext {
        val connection = objectMapper.readValue<RdbmsConnectionConfig>(resourceContext.body)

        val responseResource = ResourceContext(
                resourceContext.path,
                resourceContext.oldPath,
                objectMapper.writeValueAsString(connection)
        )

        if (!connection.isDefaultConnection) {
            return responseResource
        }

        val allConnectionResources = fileRepositoryService.getAllResourcesByType(FileType.RDBMS_CONNECTION)
        for (connectionResource in allConnectionResources) {
            val rdbmsConnectionConfig = objectMapper.readValue<RdbmsConnectionConfig>(connectionResource.body)

            if (rdbmsConnectionConfig.isDefaultConnection) {
                rdbmsConnectionConfig.isDefaultConnection = false

                fileRepositoryService.save(
                        RepositoryFileChange(
                                connectionResource.knownPath,
                                RepositoryFile(
                                        connectionResource.knownPath,
                                        objectMapper.writeValueAsString(rdbmsConnectionConfig)
                                )
                        )
                )
            }
        }

        return responseResource
    }
}