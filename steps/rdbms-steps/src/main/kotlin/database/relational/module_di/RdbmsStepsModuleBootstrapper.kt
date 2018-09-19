package database.relational.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.settings.keys.SystemSettingKeys

class RdbmsStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val assertionFunctionsModuleFactory = AssertionFunctionsModuleFactory(context)

    val jsonDiffModuleFactory = JsonDiffModuleFactory(context, assertionFunctionsModuleFactory)

    val rdbmsStepsModuleFactory = RdbmsStepsModuleFactory(context)

    //======================================== initialization ========================================//
    init {
        val jdbcDriversDir = TesterumServiceLocator.getSettingsManager()
                .getRequiredSetting(SystemSettingKeys.JDBC_DRIVERS_DIR)
                .resolvedValueAsPath

        rdbmsStepsModuleFactory.rdbmsDriverConfigCache.initialize(jdbcDriversDir)
    }

}
