package com.testerum.web_backend.controller.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_di.FileRepositoryModuleFactory
import com.testerum.model.exception.IllegalFileOperationException
import com.testerum.model.exception.ValidationException
import com.testerum.service.module_di.ServiceModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory
import com.testerum.web_backend.controller.attachments.AttachmentsController
import com.testerum.web_backend.controller.config.FileSystemController
import com.testerum.web_backend.controller.config.SettingsController
import com.testerum.web_backend.controller.config.SetupController
import com.testerum.web_backend.controller.error.ErrorController
import com.testerum.web_backend.controller.error.model.response_preparers.generic.GenericErrorResponsePreparer
import com.testerum.web_backend.controller.error.model.response_preparers.illegal_file_opperation.IllegalFileOperationPreparer
import com.testerum.web_backend.controller.error.model.response_preparers.validation.ValidationErrorResponsePreparer
import com.testerum.web_backend.controller.feature.FeatureController
import com.testerum.web_backend.controller.manual.ManualTestsController
import com.testerum.web_backend.controller.manual.ManualTestsRunnerController
import com.testerum.web_backend.controller.message.MessageController
import com.testerum.web_backend.controller.report.RunnerResultController
import com.testerum.web_backend.controller.resources.ResourcesController
import com.testerum.web_backend.controller.resources.http.HttpController
import com.testerum.web_backend.controller.resources.rdbms.RdbmsController
import com.testerum.web_backend.controller.runner.TestExecutionController
import com.testerum.web_backend.controller.runner.TestsWebSocketController
import com.testerum.web_backend.controller.step.BasicStepsController
import com.testerum.web_backend.controller.step.ComposedStepController
import com.testerum.web_backend.controller.step.StepsTreeController
import com.testerum.web_backend.controller.tag.TagsController
import com.testerum.web_backend.controller.test.TestsController
import com.testerum.web_backend.controller.vars.VariablesController

class WebBackendModuleFactory(context: ModuleFactoryContext,
                              serviceModuleFactory: ServiceModuleFactory,
                              settingsModuleFactory: SettingsModuleFactory,
                              fileRepositoryModuleFactory: FileRepositoryModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- web controllers ----------------------------------------//

    private val errorController = ErrorController(
            errorResponsePreparerMap = mapOf(
                    IllegalFileOperationException::class.java to IllegalFileOperationPreparer(),
                    ValidationException::class.java to ValidationErrorResponsePreparer()
            ),
            genericErrorResponsePreparer = GenericErrorResponsePreparer()
    )

    private val setupController = SetupController(
            settingsManager = settingsModuleFactory.settingsManager,
            stepCache = serviceModuleFactory.stepService
    )

    private val settingsController = SettingsController(
            settingsService = serviceModuleFactory.settingsService,
            stepCache = serviceModuleFactory.stepService
    )

    private val messageController = MessageController(
            messageService = serviceModuleFactory.messageService
    )

    private val fileSystemController = FileSystemController(
            fileSystemService = serviceModuleFactory.fileSystemService
    )

    private val attachmentsController = AttachmentsController(
            attachmentFileRepositoryService = fileRepositoryModuleFactory.attachmentFileRepositoryService
    )

    private val variablesController = VariablesController(
            variablesService = serviceModuleFactory.variablesService
    )

    private val resourcesController = ResourcesController(
            resourcesService = serviceModuleFactory.resourcesService
    )

    private val httpController = HttpController(
            httpClientService = serviceModuleFactory.httpClientService
    )

    private val rdbmsController = RdbmsController(
            networkService = serviceModuleFactory.networkService,
            resourcesService = serviceModuleFactory.resourcesService,
            rdbmsService = serviceModuleFactory.rdbmsService,
            rdbmsDriverConfigService = serviceModuleFactory.rdbmsDriverConfigService
    )

    private val basicStepsController = BasicStepsController(
            stepCache = serviceModuleFactory.stepService
    )

    private val composedStepController = ComposedStepController(
            stepCache = serviceModuleFactory.stepService,
            stepUpdateCompatibilityService = serviceModuleFactory.stepUpdateCompatibilityService,
            saveService = serviceModuleFactory.saveService
    )

    private val stepsTreeController = StepsTreeController(
            stepCache = serviceModuleFactory.stepService
    )

    private val testsController = TestsController(
            testsService = serviceModuleFactory.testsService,
            saveService = serviceModuleFactory.saveService
    )

    private val testExecutionController = TestExecutionController(
            testsExecutionService = serviceModuleFactory.testsExecutionService
    )

    private val testRunnerReportController = RunnerResultController(
            testRunnerResultService = serviceModuleFactory.testRunnerResultService
    )

    private val manualTestsController = ManualTestsController(
            manualTestsService = serviceModuleFactory.manualTestsService
    )

    private val manualTestsRunnerController = ManualTestsRunnerController(
            manualTestsRunnerService = serviceModuleFactory.manualTestsRunnerService
    )

    private val featureController = FeatureController(
            featureService = serviceModuleFactory.featureService
    )

    private val tagsController = TagsController(
            tagsService = serviceModuleFactory.tagsService
    )


    //---------------------------------------- list of web controllers ----------------------------------------//

    val webControllers: List<Any> = listOf(
            errorController,
            setupController,
            settingsController,
            messageController,
            fileSystemController,
            attachmentsController,
            variablesController,
            resourcesController,
            httpController,
            rdbmsController,
            basicStepsController,
            composedStepController,
            stepsTreeController,
            testsController,
            testExecutionController,
            testRunnerReportController,
            manualTestsController,
            manualTestsRunnerController,
            featureController,
            tagsController
    )


    //---------------------------------------- WebSocket controllers ----------------------------------------//

    val testsWebSocketController = TestsWebSocketController(
            testsExecutionService = serviceModuleFactory.testsExecutionService,
            objectMapper = serviceModuleFactory.testsRunnerJsonObjectMapper,
            fileRepositoryService = fileRepositoryModuleFactory.fileRepositoryService,
            testRunnerResultService = serviceModuleFactory.testRunnerResultService
    )

}
