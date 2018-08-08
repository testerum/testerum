package database.relational.module_factory

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.resource_manager.module_factory.ResourceManagerModuleFactory
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.step_transformer_utils.JsonVariableReplacer
import database.relational.connection_manager.RdbmsConnectionManager

@Suppress("unused", "LeakingThis")
class RdbmsStepsModuleFactory(context: ModuleFactoryContext,
                              fileRepositoryModuleFactory: FileRepositoryModuleFactory,
                              resourceManagerModuleFactory: ResourceManagerModuleFactory,
                              serviceModuleFactory: ServiceModuleFactory) : BaseModuleFactory(context) {

    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )
    val rdbmsConnectionManager = RdbmsConnectionManager(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            objectMapper = resourceManagerModuleFactory.resourceJsonObjectMapper,
            rdbmsService = serviceModuleFactory.rdbmsService,
            jsonVariableReplacer = jsonVariableReplacer
    )

}