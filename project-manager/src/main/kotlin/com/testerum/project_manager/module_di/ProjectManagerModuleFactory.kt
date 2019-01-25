package com.testerum.project_manager.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.module_di.FileServiceModuleFactory
import com.testerum.project_manager.ProjectManager

class ProjectManagerModuleFactory(context: ModuleFactoryContext,
                                  fileServiceModuleFactory: FileServiceModuleFactory) : BaseModuleFactory(context) {

    val projectManager = ProjectManager(
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
                StepsCache(
                        basicStepsCache = fileServiceModuleFactory.basicStepsCache,
                        composedStepFileService = fileServiceModuleFactory.composedStepFileService,
                        stepsResolver = fileServiceModuleFactory.stepsResolver,
                        warningService = fileServiceModuleFactory.warningService
                )
            }
    )

}
