package net.qutester.service.resources.validators

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.model.resources.rdbms.connection.RdbmsConnectionConfig
import javax.xml.bind.ValidationException

class RdbmsConnectionConfigValidator(val objectMapper: ObjectMapper) : ResourceValidator {

    override fun canValidate(fileType: FileType): Boolean {
        return fileType == FileType.RDBMS_CONNECTION
    }

    override fun validate(resourceContext: ResourceContext, fileType: FileType) {
        try {
            objectMapper.readValue<RdbmsConnectionConfig>(resourceContext.body)
        } catch (e: Exception) {
            throw ValidationException("The following String is not a valid Rdbms Conneciton Config Json: [${resourceContext.body}]")
        }
    }
}