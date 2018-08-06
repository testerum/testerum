package com.testerum.service.resources.validators

import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext

interface ResourceValidator {
    fun canValidate(fileType: FileType): Boolean
    fun validate(resourceContext: ResourceContext, fileType: FileType)
}