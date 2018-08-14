package database.relational.module_di

import com.testerum.api.services.TesterumServiceLocator
import com.testerum.common.json_diff.module_di.JsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_di.AssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.settings.SystemSettings
import java.nio.file.Path
import java.nio.file.Paths

class RdbmsStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    private val assertionFunctionsModuleFactory = AssertionFunctionsModuleFactory(context)

    val jsonDiffModuleFactory = JsonDiffModuleFactory(context, assertionFunctionsModuleFactory)

    private val repositoryDirectory: Path = Paths.get(
            TesterumServiceLocator.getSettingsManager().getSettingValue(SystemSettings.REPOSITORY_DIRECTORY)
    )

    private val jdbcDriversDirectory: Path = Paths.get(
            TesterumServiceLocator.getSettingsManager().getSettingValue(SystemSettings.JDBC_DRIVERS_DIRECTORY)
    )

    private val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context) { repositoryDirectory }

    val rdbmsStepsModuleFactory = RdbmsStepsModuleFactory(context, fileRepositoryModuleFactory, jdbcDriversDirectory)

}
