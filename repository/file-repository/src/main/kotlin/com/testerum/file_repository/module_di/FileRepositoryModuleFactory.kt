package com.testerum.file_repository.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.AttachmentFileRepositoryService
import com.testerum.file_repository.FileRepositoryService
import java.nio.file.Path

class FileRepositoryModuleFactory(context: ModuleFactoryContext,
                                  repositoryDirectory: Path) : BaseModuleFactory(context) {

    val fileRepositoryService = FileRepositoryService(repositoryDirectory)

    val attachmentFileRepositoryService = AttachmentFileRepositoryService(fileRepositoryService)

}
