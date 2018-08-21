package database.relational.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.api.test_context.settings.model.resolvedValueAsPath
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.settings.keys.SystemSettingKeys
import java.nio.file.Path

class RdbmsStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val assertionFunctionsModuleFactory = AssertionFunctionsModuleFactory(context)

    val jsonDiffModuleFactory = JsonDiffModuleFactory(context, assertionFunctionsModuleFactory)

    private val repositoryDirectory: Path = TesterumServiceLocator.getSettingsManager().getRequiredSetting(SystemSettingKeys.REPOSITORY_DIR).resolvedValueAsPath

    private val jdbcDriversDirectory: Path = TesterumServiceLocator.getSettingsManager().getRequiredSetting(SystemSettingKeys.JDBC_DRIVERS_DIR).resolvedValueAsPath

    private val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context) { repositoryDirectory }

    val rdbmsStepsModuleFactory = RdbmsStepsModuleFactory(context, fileRepositoryModuleFactory, jdbcDriversDirectory)

}
