package http_support.module_bootstrapper

import com.testerum.common.json_diff.module_factory.CommonJsonDiffModuleFactory
import com.testerum.common_assertion_functions.module_factory.CommonAssertionFunctionsModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.resource_manager.module_factory.ResourceManagerModuleFactory
import com.testerum.scanner.step_lib_scanner.module_factory.TesterumScannerModuleFactory
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.settings.module_factory.SettingsModuleFactory
import http_support.module_factory.HttpStepsModuleFactory

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
class HttpStepsModuleBootstrapper {

    val context = ModuleFactoryContext()

    val commonAssertionFunctionsModuleFactory = CommonAssertionFunctionsModuleFactory(context)

    val commonJsonDiffModuleFactory = CommonJsonDiffModuleFactory(context, commonAssertionFunctionsModuleFactory)

    val scannerModuleFactory = TesterumScannerModuleFactory(context)

    val settingsModuleFactory = SettingsModuleFactory(context)

    val fileRepositoryModuleFactory = FileRepositoryModuleFactory(context, settingsModuleFactory)

    val resourceManagerModuleFactory = ResourceManagerModuleFactory(context, fileRepositoryModuleFactory)

    val serviceModuleFactory = ServiceModuleFactory(context, scannerModuleFactory, resourceManagerModuleFactory, fileRepositoryModuleFactory, settingsModuleFactory)

    val httpStepsModuleFactory = HttpStepsModuleFactory(context, commonJsonDiffModuleFactory, serviceModuleFactory)

}