package com.testerum.file_repository.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.AttachmentFileRepositoryService
import com.testerum.file_repository.FileRepositoryService

class FileRepositoryModuleFactory(context: ModuleFactoryContext,
                                  getRepositoryDirectory: () -> java.nio.file.Path?) : BaseModuleFactory(context) {

    val fileRepositoryService = FileRepositoryService(getRepositoryDirectory)

    val attachmentFileRepositoryService = AttachmentFileRepositoryService(fileRepositoryService)

}
