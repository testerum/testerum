package net.testerum.resource_manager

import net.qutester.model.resources.ResourceContext
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath

class ResourceManager(private val fileRepositoryService: FileRepositoryService) {

    fun getResourceByPath(knownPath: KnownPath): ResourceContext? {
        return fileRepositoryService.getByPath(knownPath)?.mapToResource()
    }

}