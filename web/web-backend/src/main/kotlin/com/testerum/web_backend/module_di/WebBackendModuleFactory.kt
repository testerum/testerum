package com.testerum.web_backend.module_di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.cloud_client.CloudOfflineException
import com.testerum.cloud_client.error_feedback.ErrorFeedbackCloudClient
import com.testerum.cloud_client.infrastructure.CloudClientErrorResponseException
import com.testerum.cloud_client.licenses.CloudInvalidCredentialsException
import com.testerum.cloud_client.licenses.CloudNoValidLicenseException
import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.cloud_client.licenses.cache.validator.LicenseCachePeriodicValidator
import com.testerum.cloud_client.licenses.file.LicenseFileService
import com.testerum.cloud_client.licenses.parser.SignedLicensedUserProfileParser
import com.testerum.common_crypto.pem.PemMarshaller
import com.testerum.common_crypto.string_obfuscator.StringObfuscator
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.common_httpclient.HttpClientService
import com.testerum.common_httpclient.TesterumHttpClientFactory
import com.testerum.common_rdbms.JdbcDriversCache
import com.testerum.common_rdbms.RdbmsConnectionCache
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.model.exception.IllegalFileOperationException
import com.testerum.model.exception.ValidationException
import com.testerum.project_manager.module_di.ProjectManagerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory
import com.testerum.web_backend.config.TesterumWebBackendConfig
import com.testerum.web_backend.config.TesterumWebBackendConfigFactory
import com.testerum.web_backend.controllers.error.ErrorController
import com.testerum.web_backend.controllers.error.model.response_preparers.cloud_exception.CloudErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.cloud_invalid_credentials.CloudInvalidCredentialsResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.cloud_invalid_credentials.CloudNoValidLicenseResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.cloud_offline_exception.CloudOfflineResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.generic.GenericErrorResponsePreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.illegal_file_opperation.IllegalFileOperationPreparer
import com.testerum.web_backend.controllers.error.model.response_preparers.validation.ValidationErrorResponsePreparer
import com.testerum.web_backend.controllers.features.FeatureController
import com.testerum.web_backend.controllers.feedback.FeedbackController
import com.testerum.web_backend.controllers.filesystem.FileSystemController
import com.testerum.web_backend.controllers.home.HomeController
import com.testerum.web_backend.controllers.manual.ManualTestPlansController
import com.testerum.web_backend.controllers.message.MessageController
import com.testerum.web_backend.controllers.project.ProjectController
import com.testerum.web_backend.controllers.project.ProjectReloadWebSocketController
import com.testerum.web_backend.controllers.resources.ResourcesController
import com.testerum.web_backend.controllers.resources.http.HttpController
import com.testerum.web_backend.controllers.resources.rdbms.RdbmsController
import com.testerum.web_backend.controllers.results.ResultsController
import com.testerum.web_backend.controllers.results.ResultsFileServerController
import com.testerum.web_backend.controllers.runner.execution.TestExecutionController
import com.testerum.web_backend.controllers.runner.execution.TestsWebSocketController
import com.testerum.web_backend.controllers.settings.SettingsController
import com.testerum.web_backend.controllers.steps.BasicStepController
import com.testerum.web_backend.controllers.steps.ComposedStepsController
import com.testerum.web_backend.controllers.steps.StepsTreeController
import com.testerum.web_backend.controllers.tags.TagsController
import com.testerum.web_backend.controllers.tests.TestsController
import com.testerum.web_backend.controllers.user.UserController
import com.testerum.web_backend.controllers.variables.VariablesController
import com.testerum.web_backend.controllers.version_info.VersionController
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.features.FeaturesFrontendService
import com.testerum.web_backend.services.feedback.FeedbackFrontendService
import com.testerum.web_backend.services.filesystem.FileSystemFrontendService
import com.testerum.web_backend.services.home.HomeFrontendService
import com.testerum.web_backend.services.home.QuotesService
import com.testerum.web_backend.services.initializers.WebBackendInitializer
import com.testerum.web_backend.services.initializers.caches.CachesInitializer
import com.testerum.web_backend.services.initializers.caches.impl.BasicStepsCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.JdbcDriversCacheInitializer
import com.testerum.web_backend.services.initializers.caches.impl.LicenseCacheInitializer
import com.testerum.web_backend.services.initializers.info_logging.InfoLoggerInitializer
import com.testerum.web_backend.services.initializers.settings.SettingsManagerInitializer
import com.testerum.web_backend.services.manual.AutomatedToManualTestMapper
import com.testerum.web_backend.services.manual.ManualTestPlansFrontendService
import com.testerum.web_backend.services.message.MessageFrontendService
import com.testerum.web_backend.services.project.ProjectFileSystemWatcher
import com.testerum.web_backend.services.project.ProjectFrontendService
import com.testerum.web_backend.services.project.WebProjectManager
import com.testerum.web_backend.services.resources.NetworkService
import com.testerum.web_backend.services.resources.ResourcesFrontendService
import com.testerum.web_backend.services.resources.http.HttpFrontendService
import com.testerum.web_backend.services.resources.rdbms.RdbmsFrontendService
import com.testerum.web_backend.services.runner.execution.TestsExecutionFrontendService
import com.testerum.web_backend.services.runner.result.ResultsFrontendService
import com.testerum.web_backend.services.save.SaveFrontendService
import com.testerum.web_backend.services.settings.SettingsFrontendService
import com.testerum.web_backend.services.steps.BasicStepsFrontendService
import com.testerum.web_backend.services.steps.ComposedStepUpdateCompatibilityFrontendService
import com.testerum.web_backend.services.steps.ComposedStepsFrontendService
import com.testerum.web_backend.services.steps.StepsTreeFrontendService
import com.testerum.web_backend.services.tags.TagsFrontendService
import com.testerum.web_backend.services.tests.TestsFrontendService
import com.testerum.web_backend.services.user.UserFrontendService
import com.testerum.web_backend.services.user.security.AuthTokenService
import com.testerum.web_backend.services.user.security.dao.LicenseCacheTesterumUserDao
import com.testerum.web_backend.services.variables.VariablesFrontendService
import com.testerum.web_backend.services.variables.VariablesResolverService
import com.testerum.web_backend.services.version_info.VersionInfoFrontendService
import org.apache.http.client.HttpClient
import java.security.PublicKey

class WebBackendModuleFactory(context: ModuleFactoryContext,
                              settingsModuleFactory: SettingsModuleFactory,
                              fileServiceModuleFactory: FileServiceModuleFactory,
                              projectManagerModuleFactory: ProjectManagerModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- config ----------------------------------------//

    private val testerumWebBackendConfig: TesterumWebBackendConfig = TesterumWebBackendConfigFactory.createTesterumWebBackendConfig()


    //---------------------------------------- misc ----------------------------------------//

    val frontendDirs = FrontendDirs(
            testerumDirs = settingsModuleFactory.testerumDirs
    )

    private val rdbmsDriverConfigCache = JdbcDriversCache()

    val restApiObjectMapper = ObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(KotlinModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        disable(SerializationFeature.INDENT_OUTPUT)
        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }


    private val httpClient: HttpClient = TesterumHttpClientFactory.createHttpClient().also {
        context.registerShutdownHook {
            it.close()
        }
    }


    //---------------------------------------- initializers ----------------------------------------//

    private val settingsManagerInitializer = SettingsManagerInitializer(
            settingsFileService = fileServiceModuleFactory.settingsFileService,
            settingsManager = settingsModuleFactory.settingsManager,
            settingsDir = frontendDirs.getSettingsDir()
    )

    private val stepCachesInitializer = BasicStepsCacheInitializer(
            frontendDirs = frontendDirs,
            basicStepsCache = fileServiceModuleFactory.basicStepsCache
    )

    private val jdbcDriversCacheInitializer = JdbcDriversCacheInitializer(
            frontendDirs = frontendDirs,
            jdbcDriversCache = rdbmsDriverConfigCache
    )

    private val licensePublicKeyForVerification: PublicKey = PemMarshaller.parsePublicKey(
            StringObfuscator.deobfuscate(
                    "rSVJ6P64sg4d6DvbvqjX0Q==.Zn2o9B/zBEF4To/YtBuWTAhwzpxrnGwrHC2lxagQmUwhEcubVdowblp" +
                            "p6LGWaZhEGhXDmHP+AkdWOO7FqBCZRiwbxphV9AB2ADb83LZqiHJgY+S2WOdxf3xI54KHILY2Ojv9kEf" +
                            "bAlcGc8LEihSAXxlo8+l51ztfAkzW/Y0BjFcBCa7pXvQmM2dnlv6uDI1BG2W0rkX+IEdhbtXk62qJTzo" +
                            "Zt5Bx1ixiezDio64ok3YxafaceYgEVFhBmsuba598O2f1qmfeN0pyder6kRaxcQNp3OFK6xBCYFnF5Jd" +
                            "Tojd7AN+sfts5Sksw5MqmIIpQegTDqVaaEXxbN87ApBPsSywfxuBa83E3VnXh65khvn0hBPGRBOIoaXB" +
                            "398a4dtBdAzv3owH5bkx8Ue3ismygaCJlx7FiwyV8VWLYx5gjs0sbPLfoQOYAUEFPyOOsDJRrBhXN4FH" +
                            "QE2VcS+C7mW2JD3tgtopf1jhffSvCzqVoikIMGs28VYcAbQdY2viFb+03B2i3vl7BB3IaTuD8qWCRZwM" +
                            "57LJz1SllW2Lrxq08vzBBKMSvfsMyamEv3cqoION1Iiry4UTaM3ZoSO6xtz+eTyAIzLxE3DcteEzryYt" +
                            "yl1V4BvC7d/12M1Jnw+SlNfVpJVrTqHrkDXNCQcbkiDSsYjJhvZNb03Nfc2no3dUNsH8IHuyfWMcucn5" +
                            "Z4fvSPbZPOyjSvGH8D01XYvvtjx6gamQ9j69f3yxNdzPp2YMN71cHKsegYeAUSAA0lvCXHJEzOibW9mP" +
                            "9GylYWPajzm7vR3obtu5w2gkzcDbe7rA4jlUxBf/TYdMbUQgx4fywLJ98I2HnrFr0cylzdIDKyhiKZgR" +
                            "g87pag3lXdjLO57s0l0olJ/+RWN05a0tRnu+QFLt3DRzn4TjjBlJCevfdjQGgfDo2qrtL2WpBYFD5sIh" +
                            "glHcBH+72QdQ4f0Bp58uYaJ9RHwTKrkv6FENCRPnlhDKPUCQV3/YEu3hudUrgo5IMn084Nr2MQNUTZFx" +
                            "D4qfRGptyDhHEiA+MSyscLYKlpBeeJRsFx5V78mFNdFmCpcx09w=="
            )
    )

    private val signedLicensedUserProfileParser = SignedLicensedUserProfileParser(
            objectMapper = restApiObjectMapper,
            publicKeyForSignatureVerification = licensePublicKeyForVerification
    )

    private val licenseFileService = LicenseFileService(
            signedLicensedUserProfileParser = signedLicensedUserProfileParser
    )

    private val errorFeedbackCloudClient = ErrorFeedbackCloudClient (
            httpClient = httpClient,
            baseUrl = testerumWebBackendConfig.cloudFunctionsBaseUrl,
            objectMapper = restApiObjectMapper
    )

    private val licensesCloudClient = LicenseCloudClient(
            httpClient = httpClient,
            baseUrl = testerumWebBackendConfig.cloudFunctionsBaseUrl,
            objectMapper = restApiObjectMapper
    )

    val licensesCache = LicensesCache(
            licenseFileService = licenseFileService,
            licenseCloudClient = licensesCloudClient
    )

    private val licenseCachePeriodicValidator = LicenseCachePeriodicValidator(
            cronExpression =  "0 0 0 * * ? *", // every day at midnight
            licensesCache = licensesCache
    ).apply {
        context.registerShutdownHook {
            shutdown()
        }
    }

    private val licenseCacheInitializer = LicenseCacheInitializer(
            frontendDirs = frontendDirs,
            licensesCache = licensesCache,
            licenseCachePeriodicValidator = licenseCachePeriodicValidator
    )

    private val cachesInitializer = CachesInitializer(
            basicStepsCacheInitializer = stepCachesInitializer,
            jdbcDriversCacheInitializer = jdbcDriversCacheInitializer,
            licenseCacheInitializer = licenseCacheInitializer
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

    private val projectManager = projectManagerModuleFactory.projectManager.apply {
        registerOpenListener { projectRootDir, _ ->
            fileServiceModuleFactory.recentProjectsFileService.updateLastOpened(
                    projectRootDir = projectRootDir,
                    recentProjectsFile = frontendDirs.getRecentProjectsFile()
            )
        }
    }

    val projectFileSystemWatcher = ProjectFileSystemWatcher(
            fsNotifierBinariesDir = frontendDirs.getFsNotifierBinariesDir(),
            projectManager = projectManager
    ).apply {
        start()

        context.registerShutdownHook {
            shutdown()
        }
    }

    private val webProjectManager = WebProjectManager(
            projectManager = projectManager
    )

    private val versionInfoFrontendService = VersionInfoFrontendService()

    private val variablesResolverService = VariablesResolverService()

    private val feedbackFrontendService = FeedbackFrontendService (
            errorFeedbackCloudClient = errorFeedbackCloudClient
    )

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
            webProjectManager = webProjectManager,
            featureFileService = fileServiceModuleFactory.featuresFileService
    )

    private val tagsFrontendService = TagsFrontendService(
            webProjectManager = webProjectManager
    )

    private val saveFrontendService = SaveFrontendService(
            webProjectManager = webProjectManager,
            resourceFileService = fileServiceModuleFactory.resourceFileService
    )

    private val testsFrontendService = TestsFrontendService(
            webProjectManager = webProjectManager,
            warningService = fileServiceModuleFactory.warningService,
            saveFrontendService = saveFrontendService
    )

    private val basicStepsFrontendService = BasicStepsFrontendService(
            basicStepsCache = fileServiceModuleFactory.basicStepsCache
    )

    private val stepsTreeFrontendService = StepsTreeFrontendService(
            webProjectManager = webProjectManager
    )

    private val composedStepUpdateCompatibilityFrontendService = ComposedStepUpdateCompatibilityFrontendService(
            webProjectManager = webProjectManager
    )

    private val composedStepsFrontendService = ComposedStepsFrontendService(
            webProjectManager = webProjectManager,
            composedStepUpdateCompatibilityFrontendService = composedStepUpdateCompatibilityFrontendService,
            warningService = fileServiceModuleFactory.warningService,
            saveFrontendService = saveFrontendService,
            basicStepsCacheInitializer = stepCachesInitializer
    )

    private val resourcesFrontendService = ResourcesFrontendService(
            webProjectManager = webProjectManager,
            resourceFileService = fileServiceModuleFactory.resourceFileService
    )

    private val variablesFrontendService = VariablesFrontendService(
            webProjectManager = webProjectManager,
            frontendDirs = frontendDirs,
            variablesFileService = fileServiceModuleFactory.variablesFileService,
            localVariablesFileService = fileServiceModuleFactory.localVariablesFileService
    )

    private val messageFrontendService = MessageFrontendService()

    private val settingsFrontendService = SettingsFrontendService(
            frontendDirs = frontendDirs,
            settingsManager = settingsModuleFactory.settingsManager,
            settingsFileService = fileServiceModuleFactory.settingsFileService,
            cachesInitializer = cachesInitializer
    )

    private val testsExecutionFrontendService = TestsExecutionFrontendService(
            webProjectManager = webProjectManager,
            testerumDirs = settingsModuleFactory.testerumDirs,
            frontendDirs = frontendDirs,
            settingsManager = settingsModuleFactory.settingsManager,
            localVariablesFileService = fileServiceModuleFactory.localVariablesFileService,
            jsonObjectMapper = testsRunnerJsonObjectMapper
    )

    private val runnerResultFrontendService = ResultsFrontendService(
            frontendDirs = frontendDirs,
            resultsFileService = fileServiceModuleFactory.runnerResultFileService,
            webProjectManager = webProjectManager
    )

    private val networkService = NetworkService()

    private val fileSystemFrontendService = FileSystemFrontendService(
            testerumProjectFileService = fileServiceModuleFactory.testerumProjectFileService
    )

    private val httpClientService = HttpClientService(httpClient)

    private val httpFrontendService = HttpFrontendService(
            webProjectManager = webProjectManager,
            frontendDirs = frontendDirs,
            httpClientService = httpClientService,
            variablesFileService = fileServiceModuleFactory.variablesFileService,
            localVariablesFileService = fileServiceModuleFactory.localVariablesFileService,
            variablesResolverService = variablesResolverService
    )

    private val rdbmsFrontendService = RdbmsFrontendService(
            networkService = networkService,
            resourcesService = resourcesFrontendService,
            rdbmsConnectionCache = rdbmsConnectionCache,
            jdbcDriversCache = rdbmsDriverConfigCache
    )

    private val automatedToManualTestMapper = AutomatedToManualTestMapper()

    private val manualTestPlansFrontendService = ManualTestPlansFrontendService(
            webProjectManager = webProjectManager,
            automatedToManualTestMapper = automatedToManualTestMapper,
            manualTestPlanFileService = fileServiceModuleFactory.manualTestPlanFileService,
            manualTestFileService = fileServiceModuleFactory.manualTestFileService,
            testResolver = fileServiceModuleFactory.testResolver
    )

    private val projectFrontendService = ProjectFrontendService(
            frontendDirs = frontendDirs,
            recentProjectsFileService = fileServiceModuleFactory.recentProjectsFileService,
            testerumProjectFileService = fileServiceModuleFactory.testerumProjectFileService,
            projectManager = projectManager
    )

    private val quotesService = QuotesService()

    private val homeFrontendService = HomeFrontendService(
            quotesService = quotesService,
            versionInfoFrontendService = versionInfoFrontendService,
            projectFrontendService = projectFrontendService
    )

    private val testerumUserDao = LicenseCacheTesterumUserDao(
            licensesCache = licensesCache
    )

    val authTokenService = AuthTokenService(
            testerumUserDao = testerumUserDao
    )

    private val userFrontendService = UserFrontendService(
            licenseCloudClient = licensesCloudClient,
            licensesCache = licensesCache,
            trialService = fileServiceModuleFactory.trialService,
            authTokenService = authTokenService
    )

    //---------------------------------------- web controllers ----------------------------------------//

    private val errorController = ErrorController(
            errorResponsePreparerMap = mapOf(
                    IllegalFileOperationException::class.java     to IllegalFileOperationPreparer(),
                    ValidationException::class.java               to ValidationErrorResponsePreparer(),
                    CloudClientErrorResponseException::class.java to CloudErrorResponsePreparer(),
                    CloudOfflineException::class.java             to CloudOfflineResponsePreparer(),
                    CloudInvalidCredentialsException::class.java  to CloudInvalidCredentialsResponsePreparer(),
                    CloudNoValidLicenseException::class.java      to CloudNoValidLicenseResponsePreparer()
            ),
            genericErrorResponsePreparer = GenericErrorResponsePreparer()
    )

    private val versionController = VersionController(
            versionInfoFrontendService = versionInfoFrontendService
    )

    private val homeController = HomeController(
            homeFrontendService = homeFrontendService
    )

    private val projectController = ProjectController(
            projectFrontendService = projectFrontendService
    )

    private val feedbackController = FeedbackController(
            feedbackFrontendService = feedbackFrontendService
    )

    private val settingsController = SettingsController(
            settingsFrontendService = settingsFrontendService
    )

    private val messageController = MessageController(
            messageFrontendService = messageFrontendService
    )

    private val variablesController = VariablesController(
            variablesFrontendService = variablesFrontendService
    )

    private val testExecutionController = TestExecutionController(
            testsExecutionFrontendService = testsExecutionFrontendService
    )

    private val testRunnerReportController = ResultsController(
            resultsFrontendService = runnerResultFrontendService
    )

    private val resultsFileServerController = ResultsFileServerController(
            frontendDirs = frontendDirs
    )

    private val featureController = FeatureController(
            featuresFrontendService = featuresFrontendService,
            restApiObjectMapper = restApiObjectMapper
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

    private val manualExecPlansController = ManualTestPlansController(
            manualTestPlansFrontendService = manualTestPlansFrontendService
    )

    private val userController = UserController(
            userFrontendService = userFrontendService
    )


    //---------------------------------------- list of web controllers ----------------------------------------//

    val webControllers: List<Any> = listOf(
            errorController,
            versionController,
            homeController,
            projectController,
            feedbackController,
            settingsController,
            messageController,
            variablesController,
            testExecutionController,
            testRunnerReportController,
            resultsFileServerController,
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
            manualExecPlansController,
            userController
    )


    //---------------------------------------- WebSocket controllers ----------------------------------------//

    val testsWebSocketController = TestsWebSocketController(
            testsExecutionFrontendService = testsExecutionFrontendService,
            objectMapper = testsRunnerJsonObjectMapper
    )

    val projectReloadWebSocketController = ProjectReloadWebSocketController(
            projectFileSystemWatcher = projectFileSystemWatcher
    )
}
