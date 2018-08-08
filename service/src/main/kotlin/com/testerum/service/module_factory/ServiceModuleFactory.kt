package com.testerum.service.module_factory

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.model.repository.enums.FileType
import com.testerum.resource_manager.module_factory.ResourceManagerModuleFactory
import com.testerum.scanner.step_lib_scanner.StepLibraryCacheManger
import com.testerum.scanner.step_lib_scanner.module_factory.TesterumScannerModuleFactory
import com.testerum.service.config.FileSystemService
import com.testerum.service.config.SettingsService
import com.testerum.service.feature.FeatureService
import com.testerum.service.hooks.HooksService
import com.testerum.service.manual.ManualTestsRunnerService
import com.testerum.service.manual.ManualTestsService
import com.testerum.service.mapper.FileToUiStepMapper
import com.testerum.service.mapper.FileToUiTestMapper
import com.testerum.service.mapper.UiToFileStepDefMapper
import com.testerum.service.mapper.UiToFileTestMapper
import com.testerum.service.message.MessageService
import com.testerum.service.resources.ResourcesService
import com.testerum.service.resources.handler.RdbmsConnectionConfigHandler
import com.testerum.service.resources.http.HttpClientService
import com.testerum.service.resources.rdbms.NetworkService
import com.testerum.service.resources.rdbms.RdbmsDriverConfigService
import com.testerum.service.resources.rdbms.RdbmsService
import com.testerum.service.resources.validators.PathConflictValidation
import com.testerum.service.resources.validators.RdbmsConnectionConfigValidator
import com.testerum.service.scanner.ScannerService
import com.testerum.service.step.StepService
import com.testerum.service.step.StepUpdateCompatibilityService
import com.testerum.service.step.StepUpdateService
import com.testerum.service.step.impl.BasicStepsService
import com.testerum.service.step.impl.ComposedStepsService
import com.testerum.service.tags.TagsService
import com.testerum.service.tests.TestsService
import com.testerum.service.tests.resolver.TestResolver
import com.testerum.service.tests_runner.execution.TestsExecutionService
import com.testerum.service.tests_runner.result.TestRunnerResultService
import com.testerum.service.variables.VariablesService
import com.testerum.service.warning.WarningService
import com.testerum.settings.module_factory.SettingsModuleFactory
import com.testerum.settings.private_api.SettingsManagerImpl
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients

typealias ScannerServiceFactory = (SettingsManagerImpl, StepLibraryCacheManger) -> ScannerService

typealias StepServiceFactory = (BasicStepsService, ComposedStepsService, WarningService) -> StepService

@Suppress("unused", "LeakingThis")
class ServiceModuleFactory(context: ModuleFactoryContext,
                           scannerModuleFactory: TesterumScannerModuleFactory,
                           resourceManagerModuleFactory: ResourceManagerModuleFactory,
                           fileRepositoryModuleFactory: FileRepositoryModuleFactory,
                           settingsModuleFactory: SettingsModuleFactory,
                           scannerServiceFactory: ScannerServiceFactory = { settingsManager, stepLibraryCacheManger ->
                               ScannerService(settingsManager, stepLibraryCacheManger)
                                       .apply { initInBackgroundThread() }
                           },
                           stepServiceFactory: StepServiceFactory = { basicStepsService, composedStepsService, warningService ->
                               StepService(basicStepsService, composedStepsService, warningService)
                                       .apply { initInBackgroundThread() }
                           }
) : BaseModuleFactory(context) {

    //---------------------------------------- JSON ----------------------------------------//

    val jsonObjectMapper: ObjectMapper = ObjectMapperFactory.createObjectMapper()
    val testsRunnerJsonObjectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()
    val resourceJsonObjectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapperWithPrettyFormatter()


    //---------------------------------------- mappers ----------------------------------------//

    val uiToFileStepDefMapper = UiToFileStepDefMapper()
    val uiToFileTestMapper = UiToFileTestMapper(stepDefMapper = uiToFileStepDefMapper)
    val fileToUiStepMapper = FileToUiStepMapper(resourceManagerModuleFactory.resourceManager)
    val fileToUiTestMapper = FileToUiTestMapper(fileToUiStepMapper)


    //---------------------------------------- services ----------------------------------------//

    val messageService = MessageService()
    val fileSystemService = FileSystemService()
    val networkService = NetworkService()
    val warningService = WarningService()


    val settingsService = SettingsService(
            settingsManager = settingsModuleFactory.settingsManager
    )

    val variablesService = VariablesService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            jsonObjectMapper = jsonObjectMapper
    )

    val resourcesService = ResourcesService(
            validators = listOf(
                    RdbmsConnectionConfigValidator(resourceJsonObjectMapper),
                    PathConflictValidation(
                            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService
                    )
            ),
            handlers = mapOf(
                    FileType.RDBMS_CONNECTION to
                            RdbmsConnectionConfigHandler(
                                    objectMapper = resourceJsonObjectMapper,
                                    fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService
                            )
            ),
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            objectMapper = resourceJsonObjectMapper
    )

    val httpClient: HttpClient = HttpClients.createDefault().also {
        context.registerShutdownHook {
            it.close()
        }
    }

    val httpClientService = HttpClientService(httpClient)

    val rdbmsDriverConfigService = RdbmsDriverConfigService(
            settingsManager = settingsModuleFactory.settingsManager
    )

    val rdbmsService = RdbmsService(
            settingsManager = settingsModuleFactory.settingsManager,
            resourcesService = resourcesService,
            rdbmsDriverConfigService = rdbmsDriverConfigService
    )

    val scannerService = scannerServiceFactory(settingsModuleFactory.settingsManager, scannerModuleFactory.stepLibraryCacheManger)

    val hooksService = HooksService(scannerService)

    val basicStepsService = BasicStepsService(
            scannerService = scannerService,
            settingsManager = settingsModuleFactory.settingsManager
    )

    val composedStepsService = ComposedStepsService(
            uiToFileStepDefMapper = uiToFileStepDefMapper,
            fileToUiStepMapper = fileToUiStepMapper,
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            warningService = warningService
    )

    val stepService = stepServiceFactory(basicStepsService, composedStepsService, warningService)

    val testResolver = TestResolver(stepService)

    val testsService = TestsService(
            testResolver = testResolver,
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            resourcesService = resourcesService,
            uiToFileTestMapper = uiToFileTestMapper,
            fileToUiTestMapper = fileToUiTestMapper,
            warningService = warningService
    )

    val manualTestsService = ManualTestsService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService
    )

    val manualTestsRunnerService = ManualTestsRunnerService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService
    )

    val testsExecutionService = TestsExecutionService(
            settingsManager = settingsModuleFactory.settingsManager,
            jsonObjectMapper = testsRunnerJsonObjectMapper
    )

    val testRunnerResultService = TestRunnerResultService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            objectMapper = fileRepositoryModuleFactory.fileRepositoryObjectMapper
    )

    val featureService = FeatureService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            attachmentFileRepositoryService = fileRepositoryModuleFactory.attachmentFileRepositoryService,
            testsService = testsService
    )

    val stepUpdateCompatibilityService = StepUpdateCompatibilityService(
            stepService = stepService,
            testsService = testsService
    )

    val stepUpdateService = StepUpdateService(
            stepService = stepService,
            composedStepsService = composedStepsService,
            stepUpdateCompatibilityService = stepUpdateCompatibilityService,
            testsService = testsService,
            warningService = warningService
    )

    val tagsService = TagsService(
            featureService = featureService,
            testsService = testsService,
            stepService = stepService
    )

}