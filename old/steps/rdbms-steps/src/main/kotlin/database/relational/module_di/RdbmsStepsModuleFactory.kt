package database.relational.module_di

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.step_transformer_utils.JsonVariableReplacer
import database.relational.connection_manager.RdbmsConnectionManager

class RdbmsStepsModuleFactory(context: ModuleFactoryContext) : BaseModuleFactory(context) {

    val jsonVariableReplacer = JsonVariableReplacer(
            TesterumServiceLocator.getTestVariables()
    )

    val rdbmsDriverConfigCache = JdbcDriversCache()

    private val rdbmsConnectionCache = RdbmsConnectionCache(
            jdbcDriversCache = rdbmsDriverConfigCache
    )

    val rdbmsConnectionManager = RdbmsConnectionManager(
            rdbmsConnectionCache = rdbmsConnectionCache
    )

}
