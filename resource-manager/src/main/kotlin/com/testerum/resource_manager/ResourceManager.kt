package com.testerum.resource_manager

import com.testerum.file_repository.FileRepositoryService
import com.testerum.file_repository.model.KnownPath
import com.testerum.model.resources.ResourceContext

class ResourceManager(private val fileRepositoryService: FileRepositoryService) {

    fun getResourceByPath(knownPath: KnownPath): ResourceContext? {
        return fileRepositoryService.getByPath(knownPath)?.mapToResource()
    }

}