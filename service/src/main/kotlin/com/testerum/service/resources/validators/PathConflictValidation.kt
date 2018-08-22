package com.testerum.service.resources.validators

import com.testerum.service.file_repository.FileRepositoryService
import com.testerum.service.file_repository.model.KnownPath
import com.testerum.model.exception.ValidationException
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import com.testerum.service.resources.util.isCreateResource
import com.testerum.service.resources.util.isRelocateResource

class PathConflictValidation(val fileRepositoryService: FileRepositoryService) : ResourceValidator {

    override fun canValidate(fileType: FileType): Boolean {
        return true
    }

    override fun validate(resourceContext: ResourceContext, fileType: FileType) {
        if(resourceContext.isCreateResource() || resourceContext.isRelocateResource()) {
            if (resourceContext.isRelocateResource() && resourceContext.path.toString().equals(resourceContext.oldPath.toString(), true)) {
                return
            }

            if(fileRepositoryService.existResourceAtPath(KnownPath(resourceContext.path, fileType))) {
                throw ValidationException().addFiledValidationError(
                        "name",
                        "a_resource_with_the_same_name_already_exist"
                )
            }
        }
    }
}
