package com.testerum.service.file_repository.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.service.file_repository.AttachmentFileRepositoryService
import com.testerum.service.file_repository.FileRepositoryService

class FileRepositoryModuleFactory(context: ModuleFactoryContext,
                                  getRepositoryDirectory: () -> java.nio.file.Path?) : BaseModuleFactory(context) {

    val fileRepositoryService = FileRepositoryService(getRepositoryDirectory)

    val attachmentFileRepositoryService = AttachmentFileRepositoryService(fileRepositoryService)

}
