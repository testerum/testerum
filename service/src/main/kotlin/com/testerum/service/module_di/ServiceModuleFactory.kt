package com.testerum.service.module_di

import com.fasterxml.jackson.databind.ObjectMapper
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.common_rdbms.RdbmsDriverConfigService
import com.testerum.common_rdbms.RdbmsService
import com.testerum.service.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.model.repository.enums.FileType
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
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
import com.testerum.service.resources.rdbms.NetworkService
import com.testerum.service.resources.validators.PathConflictValidation
import com.testerum.service.resources.validators.RdbmsConnectionConfigValidator
import com.testerum.service.save.SaveService
import com.testerum.service.scanner.ScannerService
import com.testerum.service.settings.FileSystemService
import com.testerum.service.step.StepCache
import com.testerum.service.step.StepUpdateCompatibilityService
import com.testerum.service.step.impl.BasicStepsService
import com.testerum.service.step.impl.ComposedStepsService
import com.testerum.service.tags.TagsService
import com.testerum.service.tests.TestsService
import com.testerum.service.tests.resolver.TestResolver
import com.testerum.service.tests_runner.execution.TestsExecutionService
import com.testerum.service.tests_runner.result.TestRunnerResultService
import com.testerum.service.variables.VariablesService
import com.testerum.service.warning.WarningService
import com.testerum.settings.module_di.SettingsModuleFactory
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients
import java.nio.file.Path

typealias StepServiceFactory = (BasicStepsService, ComposedStepsService, WarningService) -> StepCache

class ServiceModuleFactory(context: ModuleFactoryContext,
                           scannerModuleFactory: TesterumScannerModuleFactory,
                           fileRepositoryModuleFactory: FileRepositoryModuleFactory,
                           settingsModuleFactory: SettingsModuleFactory,
                           jdbcDriversDirectory: Path,
                           stepServiceFactory: StepServiceFactory = { basicStepsService, composedStepsService, warningService ->
                               StepCache(basicStepsService, composedStepsService, warningService)
                                       .apply { initInBackgroundThread() }
                           }
) : BaseModuleFactory(context) {

    //---------------------------------------- JSON ----------------------------------------//

    val testsRunnerJsonObjectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()
    private val resourceJsonObjectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapperWithPrettyFormatter()


    //---------------------------------------- mappers ----------------------------------------//

    private val uiToFileStepDefMapper = UiToFileStepDefMapper()
    private val uiToFileTestMapper = UiToFileTestMapper(stepDefMapper = uiToFileStepDefMapper)
    private val fileToUiStepMapper = FileToUiStepMapper(fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService)
    private val fileToUiTestMapper = FileToUiTestMapper(fileToUiStepMapper)


    //---------------------------------------- services ----------------------------------------//

    val messageService = MessageService()
    val fileSystemService = FileSystemService()
    val networkService = NetworkService()
    private val warningService = WarningService()

    // todo: remove "service" module and split per functionality instead (e.g. the settings file stuff is only relevant to the web)

    val variablesService = VariablesService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            jsonObjectMapper = ObjectMapperFactory.VARIABLES_OBJECT_MAPPER
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

    private val httpClient: HttpClient = HttpClients.createDefault().also {
        context.registerShutdownHook {
            it.close()
        }
    }

    val httpClientService = HttpClientService(httpClient)

    val rdbmsDriverConfigService = RdbmsDriverConfigService(
            jdbcDriversDirectory = jdbcDriversDirectory
    )

    val rdbmsService = RdbmsService(
            rdbmsDriverConfigService = rdbmsDriverConfigService,
            jdbcDriversDirectory = jdbcDriversDirectory
    )

    val scannerService = ScannerService(
            settingsManager = settingsModuleFactory.settingsManager,
            stepLibraryCacheManger = scannerModuleFactory.stepLibraryCacheManger
    )

    val hooksService = HooksService(scannerService)

    private val basicStepsService = BasicStepsService(
            scannerService = scannerService,
            settingsManager = settingsModuleFactory.settingsManager
    )

    private val composedStepsService = ComposedStepsService(
            fileToUiStepMapper = fileToUiStepMapper,
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            warningService = warningService
    )

    val stepService = stepServiceFactory(basicStepsService, composedStepsService, warningService)

    private val testResolver = TestResolver(stepService)

    val testsService = TestsService(
            testResolver = testResolver,
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
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
            testsService = testsService,
            settingsManager = settingsModuleFactory.settingsManager,
            jsonObjectMapper = testsRunnerJsonObjectMapper
    )

    val testRunnerResultService = TestRunnerResultService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            objectMapper = ObjectMapperFactory.RUNNER_EVENT_OBJECT_MAPPER
    )

    val featureService = FeatureService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            attachmentFileRepositoryService = fileRepositoryModuleFactory.attachmentFileRepositoryService,
            testsService = testsService
    )

    val stepUpdateCompatibilityService = StepUpdateCompatibilityService(
            stepCache = stepService,
            testsService = testsService
    )

    val tagsService = TagsService(
            featureService = featureService,
            testsService = testsService,
            stepCache = stepService
    )

    val saveService = SaveService(
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            testsService = testsService,
            uiToFileTestMapper = uiToFileTestMapper,
            stepUpdateCompatibilityService = stepUpdateCompatibilityService,
            stepCache = stepService,
            uiToFileStepDefMapper = uiToFileStepDefMapper,
            resourcesService = resourcesService,
            warningService = warningService
    )

}
