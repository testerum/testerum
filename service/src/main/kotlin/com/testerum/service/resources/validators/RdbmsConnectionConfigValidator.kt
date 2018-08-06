package com.testerum.service.resources.validators

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.model.resources.rdbms.connection.RdbmsConnectionConfig
import javax.xml.bind.ValidationException

class RdbmsConnectionConfigValidator(val objectMapper: ObjectMapper) : ResourceValidator {

    override fun canValidate(fileType: FileType): Boolean {
        return fileType == FileType.RDBMS_CONNECTION
    }

    override fun validate(resourceContext: ResourceContext, fileType: FileType) {
        try {
            objectMapper.readValue<RdbmsConnectionConfig>(resourceContext.body)
        } catch (e: Exception) {
            throw ValidationException("The following String is not a valid Rdbms Connection Config Json: [${resourceContext.body}]")
        }
    }
}