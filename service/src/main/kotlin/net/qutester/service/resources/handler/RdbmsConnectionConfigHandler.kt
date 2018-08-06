package net.qutester.service.resources.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.RepositoryFile
import com.testerum.file_repository.model.RepositoryFileChange
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig

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