package database.relational.module_di

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext

class RdbmsStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val assertionFunctionsModuleFactory = AssertionFunctionsModuleFactory(context)

    val jsonDiffModuleFactory = JsonDiffModuleFactory(context, assertionFunctionsModuleFactory)

    val rdbmsStepsModuleFactory = RdbmsStepsModuleFactory(context)

    //======================================== initialization ========================================//
    init {
        val jdbcDriversDir = TesterumServiceLocator.getTesterumDirs().getJdbcDriversDir()

        rdbmsStepsModuleFactory.rdbmsDriverConfigCache.initialize(jdbcDriversDir)
    }

}
