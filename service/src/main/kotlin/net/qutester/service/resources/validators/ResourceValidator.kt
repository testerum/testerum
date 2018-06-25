package net.qutester.service.resources.validators

import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext

interface ResourceValidator {
    fun canValidate(fileType: FileType): Boolean
    fun validate(resourceContext: ResourceContext, fileType: FileType)
}