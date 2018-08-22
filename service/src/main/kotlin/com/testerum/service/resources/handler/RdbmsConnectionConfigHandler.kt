package com.testerum.service.resources.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.file_repository.FileRepositoryService
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

        return responseResource
    }
}
