package com.testerum.web_backend.module_di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.model.exception.IllegalFileOperationException
import com.testerum.model.exception.ValidationException
import com.testerum.settings.module_di.SettingsModuleFactory
import com.testerum.web_backend.controllers.error.ErrorController
import com.testerum.web_backend.controllers.error.model.response_preparers.generic.GenericErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.illegal_file_opperation.IllegalFileOperationPreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponsePreparer
import com.testerum.web_backend.controllers.features.FeatureAttachmentsController
import com.testerum.web_backend.controllers.features.FeatureController
import com.testerum.web_backend.controllers.filesystem.FileSystemController
import com.testerum.web_backend.controllers.manual.ManualExecPlansController
import com.testerum.web_backend.controllers.message.MessageController
import com.testerum.web_backend.controllers.resources.ResourcesController
import com.testerum.web_backend.controllers.resources.http.HttpController
import com.testerum.web_backend.controllers.resources.rdbms.RdbmsController
import com.testerum.web_backend.controllers.runner.execution.TestExecutionController
import com.testerum.web_backend.controllers.runner.execution.TestsWebSocketController
import com.testerum.web_backend.controllers.runner.result.RunnerResultController
import com.testerum.web_backend.controllers.settings.SettingsController
import com.testerum.web_backend.controllers.setup.SetupController
import com.testerum.web_backend.controllers.steps.BasicStepController
import com.testerum.web_backend.controllers.steps.ComposedStepsController
import com.testerum.web_backend.controllers.steps.StepsTreeController
import com.testerum.web_backend.controllers.tags.TagsController
import com.testerum.web_backend.controllers.tests.TestsController
import com.testerum.web_backend.controllers.variables.VariablesController
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.features.FeaturesFrontendService
import com.testerum.web_backend.services.filesystem.FileSystemFrontendService
import com.testerum.web_backend.services.initializers.WebBackendInitializer
import com.testerum.web_backend.services.initializers.caches.CachesInitializer
import com.testerum.web_backend.services.initializers.caches.impl.FeaturesCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.JdbcDriversCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.StepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.TestsCacheInitializer
import com.testerum.web_backend.services.initializers.info_logging.InfoLoggerInitializer
import com.testerum.web_backend.services.initializers.settings.SettingsManagerInitializer
import com.testerum.web_backend.services.message.MessageFrontendService
import com.testerum.web_backend.services.resources.NetworkService
import com.testerum.web_backend.services.resources.ResourcesFrontendService
import com.testerum.web_backend.services.resources.http.HttpFrontendService
import com.testerum.web_backend.services.resources.rdbms.RdbmsFrontendService
import com.testerum.web_backend.services.runner.execution.TestsExecutionFrontendService
import com.testerum.web_backend.services.runner.result.RunnerResultFrontendService
import com.testerum.web_backend.services.save.SaveFrontendService
import com.testerum.web_backend.services.settings.SettingsFrontendService
import com.testerum.web_backend.services.setup.SetupFrontendService
import com.testerum.web_backend.services.steps.BasicStepsFrontendService
import com.testerum.web_backend.services.steps.ComposedStepUpdateCompatibilityFrontendService
import com.testerum.web_backend.services.steps.ComposedStepsFrontendService
import com.testerum.web_backend.services.steps.StepsTreeFrontendService
import com.testerum.web_backend.services.tags.TagsFrontendService
import com.testerum.web_backend.services.tests.TestsFrontendService
import com.testerum.web_backend.services.variables.VariablesFrontendService
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClients

class WebBackendModuleFactory(context: ModuleFactoryContext,
                              settingsModuleFactory: SettingsModuleFactory,
                              fileServiceModuleFactory: FileServiceModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- misc ----------------------------------------//

    val frontendDirs = FrontendDirs(
            settingsManager = settingsModuleFactory.settingsManager
    )

    private val rdbmsDriverConfigCache = JdbcDriversCache()


    //---------------------------------------- initializers ----------------------------------------//

    private val settingsManagerInitializer = SettingsManagerInitializer(
            settingsFileService = fileServiceModuleFactory.settingsFileService,
            settingsDir = frontendDirs.getSettingsDir(),
            settingsManager = settingsModuleFactory.settingsManager
    )

    private val stepCachesInitializer = StepsCacheInitializer(
            frontendDirs = frontendDirs,
            stepsCache = fileServiceModuleFactory.stepsCache
    )

    private val testsCacheInitializer = TestsCacheInitializer(
            frontendDirs = frontendDirs,
            testsCache = fileServiceModuleFactory.testsCache
    )

    private val featuresCacheInitializer = FeaturesCacheInitializer(
            frontendDirs = frontendDirs,
            featuresCache = fileServiceModuleFactory.featuresCache
    )

    private val jdbcDriversCacheInitializer = JdbcDriversCacheInitializer(
            frontendDirs = frontendDirs,
            jdbcDriversCache = rdbmsDriverConfigCache
    )

    private val cachesInitializer = CachesInitializer(
            stepsCacheInitializer = stepCachesInitializer,
            testsCacheInitializer = testsCacheInitializer,
            featuresCacheInitializer = featuresCacheInitializer,
            jdbcDriversCacheInitializer = jdbcDriversCacheInitializer
    )

    private val infoLoggerInitializer = InfoLoggerInitializer(
            settingsManager = settingsModuleFactory.settingsManager,
            frontendDirs = frontendDirs
    )

    val webBackendInitializer = WebBackendInitializer(
            settingsManagerInitializer = settingsManagerInitializer,
            cachesInitializer = cachesInitializer,
            infoLoggerInitializer = infoLoggerInitializer
    )


    //---------------------------------------- services ----------------------------------------//

    private val testsRunnerJsonObjectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        disable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    private val rdbmsConnectionCache = RdbmsConnectionCache(
            jdbcDriversCache = rdbmsDriverConfigCache
    )

    private val featuresFrontendService = FeaturesFrontendService(
            frontendDirs = frontendDirs,
            featuresCache = fileServiceModuleFactory.featuresCache,
            testsCache = fileServiceModuleFactory.testsCache,
            featureFileService = fileServiceModuleFactory.featuresFileService
    )

    private val tagsFrontendService = TagsFrontendService(
            featuresCache = fileServiceModuleFactory.featuresCache,
            testsCache = fileServiceModuleFactory.testsCache,
            stepsCache = fileServiceModuleFactory.stepsCache
    )

    private val saveFrontendService = SaveFrontendService(
            frontendDirs = frontendDirs,
            testsCache = fileServiceModuleFactory.testsCache,
            stepsCache = fileServiceModuleFactory.stepsCache,
            resourceFileService = fileServiceModuleFactory.resourceFileService
    )

    private val testsFrontendService = TestsFrontendService(
            testsCache = fileServiceModuleFactory.testsCache,
            warningService = fileServiceModuleFactory.warningService,
            saveFrontendService = saveFrontendService
    )

    private val basicStepsFrontendService = BasicStepsFrontendService(
            stepsCache = fileServiceModuleFactory.stepsCache
    )

    private val stepsTreeFrontendService = StepsTreeFrontendService(
            stepsCache = fileServiceModuleFactory.stepsCache
    )

    private val composedStepUpdateCompatibilityFrontendService = ComposedStepUpdateCompatibilityFrontendService(
            stepsCache = fileServiceModuleFactory.stepsCache,
            testsCache = fileServiceModuleFactory.testsCache
    )

    private val composedStepsFrontendService = ComposedStepsFrontendService(
            stepsCache = fileServiceModuleFactory.stepsCache,
            composedStepUpdateCompatibilityFrontendService = composedStepUpdateCompatibilityFrontendService,
            warningService = fileServiceModuleFactory.warningService,
            saveFrontendService = saveFrontendService
    )

    private val resourcesFrontendService = ResourcesFrontendService(
            frontendDirs = frontendDirs,
            resourceFileService = fileServiceModuleFactory.resourceFileService
    )

    private val variablesFrontendService = VariablesFrontendService(
            frontendDirs = frontendDirs,
            variablesFileService = fileServiceModuleFactory.variablesFileService
    )

    private val messageFrontendService = MessageFrontendService()

    private val setupFrontendService = SetupFrontendService(
            frontendDirs = frontendDirs,
            settingsManager = settingsModuleFactory.settingsManager,
            settingsFileService = fileServiceModuleFactory.settingsFileService,
            webBackendInitializer = webBackendInitializer
    )

    private val settingsFrontendService = SettingsFrontendService(
            frontendDirs = frontendDirs,
            settingsManager = settingsModuleFactory.settingsManager,
            settingsFileService = fileServiceModuleFactory.settingsFileService,
            cachesInitializer = cachesInitializer
    )

    private val testsExecutionFrontendService = TestsExecutionFrontendService(
            testsCache = fileServiceModuleFactory.testsCache,
            settingsManager = settingsModuleFactory.settingsManager,
            jsonObjectMapper = testsRunnerJsonObjectMapper,
            frontendDirs = frontendDirs
    )

    private val runnerResultFrontendService = RunnerResultFrontendService(
            frontendDirs = frontendDirs,
            runnerResultFileService = fileServiceModuleFactory.runnerResultFileService
    )

    private val networkService = NetworkService()

    private val fileSystemFrontendService = FileSystemFrontendService()

    private val httpClient: HttpClient = HttpClients.createDefault().also {
        context.registerShutdownHook {
            it.close()
        }
    }

    private val httpClientService = HttpClientService(httpClient)

    private val httpFrontendService = HttpFrontendService(
            httpClientService = httpClientService
    )

    private val rdbmsFrontendService = RdbmsFrontendService(
            networkService = networkService,
            resourcesService = resourcesFrontendService,
            rdbmsConnectionCache = rdbmsConnectionCache,
            jdbcDriversCache = rdbmsDriverConfigCache
    )


    //---------------------------------------- web controllers ----------------------------------------//

    private val errorController = ErrorController(
            errorResponsePreparerMap = mapOf(
                    IllegalFileOperationException::class.java to IllegalFileOperationPreparer(),
                    ValidationException::class.java to ValidationErrorResponsePreparer()
            ),
            genericErrorResponsePreparer = GenericErrorResponsePreparer()
    )

    private val setupController = SetupController(
            setupFrontendService = setupFrontendService
    )

    private val settingsController = SettingsController(
            settingsFrontendService = settingsFrontendService
    )

    private val messageController = MessageController(
            messageFrontendService = messageFrontendService
    )

    private val attachmentsController = FeatureAttachmentsController(
            featuresFrontendService = featuresFrontendService
    )

    private val variablesController = VariablesController(
            variablesFrontendService = variablesFrontendService
    )

    private val testExecutionController = TestExecutionController(
            testsExecutionFrontendService = testsExecutionFrontendService
    )

    private val testRunnerReportController = RunnerResultController(
            runnerResultFrontendService = runnerResultFrontendService
    )

    private val featureController = FeatureController(
            featuresFrontendService = featuresFrontendService
    )

    private val tagsController = TagsController(
            tagsFrontendService = tagsFrontendService
    )

    private val testsController = TestsController(
            testsFrontendService = testsFrontendService
    )

    private val basicStepController = BasicStepController(
            basicStepsFrontendService = basicStepsFrontendService
    )

    private val stepsTreeController = StepsTreeController(
            stepsTreeFrontendService = stepsTreeFrontendService
    )

    private val composedStepsController = ComposedStepsController(
            composedStepsFrontendService = composedStepsFrontendService
    )

    private val resourcesController = ResourcesController(
            resourcesFrontendService = resourcesFrontendService
    )

    private val httpController = HttpController(
            httpFrontendService = httpFrontendService
    )

    private val rdbmsController = RdbmsController(
            rdbmsFrontendService = rdbmsFrontendService
    )

    private val fileSystemController = FileSystemController(
            fileSystemFrontendService = fileSystemFrontendService
    )

    private val manualExecPlansController = ManualExecPlansController()


    //---------------------------------------- list of web controllers ----------------------------------------//

    val webControllers: List<Any> = listOf(
            errorController,
            setupController,
            settingsController,
            messageController,
            attachmentsController,
            variablesController,
            testExecutionController,
            testRunnerReportController,
            featureController,
            tagsController,
            testsController,
            basicStepController,
            stepsTreeController,
            composedStepsController,
            resourcesController,
            httpController,
            rdbmsController,
            fileSystemController,
            manualExecPlansController
    )


    //---------------------------------------- WebSocket controllers ----------------------------------------//

    val testsWebSocketController = TestsWebSocketController(
            testsExecutionFrontendService = testsExecutionFrontendService,
            objectMapper = testsRunnerJsonObjectMapper,
            runnerResultFrontendService = runnerResultFrontendService
    )

}
