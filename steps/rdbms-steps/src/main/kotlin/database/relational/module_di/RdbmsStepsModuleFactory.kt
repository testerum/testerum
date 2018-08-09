package database.relational.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.common_rdbms.RdbmsDriverConfigService
import com.testerum.common_rdbms.RdbmsService
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.step_transformer_utils.JsonVariableReplacer
import database.relational.connection_manager.RdbmsConnectionManager
import java.nio.file.Path

class RdbmsStepsModuleFactory(context: ModuleFactoryContext,
                              fileRepositoryModuleFactory: FileRepositoryModuleFactory,
                              jdbcDriversDirectory: Path) : BaseModuleFactory(context) {

    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )

    private val rdbmsDriverConfigService = RdbmsDriverConfigService(
            jdbcDriversDirectory = jdbcDriversDirectory
    )

    private val rdbmsService = RdbmsService(
            jdbcDriversDirectory = jdbcDriversDirectory,
            rdbmsDriverConfigService = rdbmsDriverConfigService
    )

    val rdbmsConnectionManager = RdbmsConnectionManager(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            objectMapper = ObjectMapperFactory.RESOURCE_OBJECT_MAPPER,
            rdbmsService = rdbmsService,
            jsonVariableReplacer = jsonVariableReplacer
    )

}