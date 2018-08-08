package com.testerum.web_backend.controller.module_factory

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.module_factory.FileRepositoryModuleFactory
import com.testerum.model.exception.IllegalFileOperationException
import com.testerum.model.exception.ValidationException
import com.testerum.service.message.MessageService
import com.testerum.service.module_factory.ServiceModuleFactory
import com.testerum.settings.module_factory.SettingsModuleFactory
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

@Suppress("unused", "LeakingThis")
class WebBackendModuleFactory(context: ModuleFactoryContext,
                              serviceModuleFactory: ServiceModuleFactory,
                              settingsModuleFactory: SettingsModuleFactory,
                              fileRepositoryModuleFactory: FileRepositoryModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- web controllers ----------------------------------------//

    val errorController = ErrorController(
            errorResponsePreparerMap = mapOf(
                    IllegalFileOperationException::class.java to IllegalFileOperationPreparer(),
                    ValidationException::class.java to ValidationErrorResponsePreparer()
            ),
            genericErrorResponsePreparer = GenericErrorResponsePreparer()
    )

    val setupController = SetupController(
            settingsManager = settingsModuleFactory.settingsManager,
            stepService = serviceModuleFactory.stepService
    )

    val settingsController = SettingsController(
            settingsService = serviceModuleFactory.settingsService,
            stepService = serviceModuleFactory.stepService
    )

    val messageController = MessageController(
            messageService = MessageService()
    )

    val fileSystemController = FileSystemController(
            fileSystemService = serviceModuleFactory.fileSystemService
    )

    val attachmentsController = AttachmentsController(
            attachmentFileRepositoryService = fileRepositoryModuleFactory.attachmentFileRepositoryService
    )

    val variablesController = VariablesController(
            variablesService = serviceModuleFactory.variablesService
    )

    val resourcesController = ResourcesController(
            resourcesService = serviceModuleFactory.resourcesService
    )

    val httpController = HttpController(
            httpClientService = serviceModuleFactory.httpClientService
    )

    val rdbmsController = RdbmsController(
            networkService = serviceModuleFactory.networkService,
            rdbmsService = serviceModuleFactory.rdbmsService,
            rdbmsDriverConfigService = serviceModuleFactory.rdbmsDriverConfigService
    )

    val basicStepsController = BasicStepsController(
            stepService = serviceModuleFactory.stepService
    )

    val composedStepController = ComposedStepController(
            stepService = serviceModuleFactory.stepService,
            stepUpdateService = serviceModuleFactory.stepUpdateService,
            stepUpdateCompatibilityService = serviceModuleFactory.stepUpdateCompatibilityService
    )

    val stepsTreeController = StepsTreeController(
            stepService = serviceModuleFactory.stepService
    )

    val testsController = TestsController(
            testsService = serviceModuleFactory.testsService
    )

    val testExecutionController = TestExecutionController(
            testsExecutionService = serviceModuleFactory.testsExecutionService
    )

    val testRunnerReportController = RunnerResultController(
            testRunnerResultService = serviceModuleFactory.testRunnerResultService
    )

    val manualTestsController = ManualTestsController(
            manualTestsService = serviceModuleFactory.manualTestsService
    )

    val manualTestsRunnerController = ManualTestsRunnerController(
            manualTestsRunnerService = serviceModuleFactory.manualTestsRunnerService
    )

    val featureController = FeatureController(
            featureService = serviceModuleFactory.featureService
    )

    val tagsController = TagsController(
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