package com.testerum.project_manager.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.ProjectManager
import com.testerum.settings.module_di.SettingsModuleFactory

class ProjectManagerModuleFactory(
    context: ModuleFactoryContext,
    fileServiceModuleFactory: FileServiceModuleFactory,
    settingsModuleFactory: SettingsModuleFactory,
    packagesWithAnnotations: List<String>,
) : BaseModuleFactory(context) {

    val projectManager = ProjectManager(
        testerumProjectFileService = fileServiceModuleFactory.testerumProjectFileService,
        packagesWithAnnotations = packagesWithAnnotations,
        createFeaturesCache = {
            FeaturesCache(
                featureFileService = fileServiceModuleFactory.featuresFileService
            )
        },
        createTestsCache = { projectServices ->
            TestsCache(
                testFileService = fileServiceModuleFactory.testsFileService,
                testResolver = fileServiceModuleFactory.testResolver,
                warningService = fileServiceModuleFactory.warningService,
                getStepsCache = { projectServices.getStepsCache() }
            )
        },
        createStepsCache = {
            val basicStepsCache = BasicStepsCache(
                extensionsLoaderService = fileServiceModuleFactory.extensionsLoaderService,
                settingsManager = settingsModuleFactory.settingsManager
            )

            StepsCache(
                basicStepsCache = basicStepsCache,
                composedStepFileService = fileServiceModuleFactory.composedStepFileService,
                stepsResolver = fileServiceModuleFactory.stepsResolver,
                warningService = fileServiceModuleFactory.warningService
            )
        }
    )

}
