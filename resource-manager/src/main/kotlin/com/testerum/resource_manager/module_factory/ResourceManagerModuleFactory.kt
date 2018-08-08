package com.testerum.resource_manager.module_factory

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.resource_manager.ResourceManager

@Suppress("unused", "LeakingThis")
class ResourceManagerModuleFactory(context: ModuleFactoryContext,
                                   fileRepositoryModuleFactory: FileRepositoryModuleFactory) : BaseModuleFactory(context) {

    val resourceManager = ResourceManager(fileRepositoryModuleFactory.fileRepositoryService)

    val resourceJsonObjectMapper = ObjectMapperFactory.createKotlinObjectMapperWithPrettyFormatter()

}
