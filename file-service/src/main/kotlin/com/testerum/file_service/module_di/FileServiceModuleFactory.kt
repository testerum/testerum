package com.testerum.file_service.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.caches.RecentProjectsCache
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.caches.resolved.resolvers.ArgsResolver
import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.ComposedStepFileService
import com.testerum.file_service.file.FeatureFileService
import com.testerum.file_service.file.ManualTestFileService
import com.testerum.file_service.file.ManualTestPlanFileService
import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.ResourceFileService
import com.testerum.file_service.file.ResultsFileService
import com.testerum.file_service.file.SettingsFileService
import com.testerum.file_service.file.TestFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.file_service.mapper.business_to_file.BusinessToFileFeatureMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileStepMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileTestMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFilePhaseMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualStepCallMapper
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualStepStatusMapper
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualTestMapper
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualTestPlanMapper
import com.testerum.file_service.mapper.business_to_file.manual.BusinessToFileManualTestStatusMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessFeatureMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessStepMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessTestMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessPhaseMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualStepCallMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualStepStatusMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestPlanMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestStatusMapper
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory

class FileServiceModuleFactory(context: ModuleFactoryContext,
                               settingsModuleFactory: SettingsModuleFactory,
                               scannerModuleFactory: TesterumScannerModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- mapper: file -> business ----------------------------------------//

    private val fileToBusinessStepPhaseMapper = FileToBusinessPhaseMapper()

    private val fileToBusinessStepCallMapper = FileToBusinessStepCallMapper(
            phaseMapper = fileToBusinessStepPhaseMapper
    )

    private val fileToBusinessStepMapper = FileToBusinessStepMapper(
            phaseMapper = fileToBusinessStepPhaseMapper,
            callsMapper = fileToBusinessStepCallMapper
    )

    private val fileToBusinessTestMapper = FileToBusinessTestMapper(
            stepCallMapper = fileToBusinessStepCallMapper
    )

    private val fileToBusinessFeatureMapper = FileToBusinessFeatureMapper()

    private val fileToBusinessManualTestPlanMapper = FileToBusinessManualTestPlanMapper()

    private val fileToBusinessManualTestStatusMapper = FileToBusinessManualTestStatusMapper()

    private val fileToBusinessManualStepStatusMapper = FileToBusinessManualStepStatusMapper()

    private val fileToBusinessManualStepCallMapper = FileToBusinessManualStepCallMapper(
            stepCallMapper = fileToBusinessStepCallMapper,
            stepStatusMapper = fileToBusinessManualStepStatusMapper
    )

    private val fileToBusinessManualTestMapper = FileToBusinessManualTestMapper(
            testStatusMapper = fileToBusinessManualTestStatusMapper,
            stepCallMapper = fileToBusinessManualStepCallMapper
    )


    //---------------------------------------- mapper: business -> file ----------------------------------------//

    private val businessToFilePhaseMapper = BusinessToFilePhaseMapper()

    private val businessToFileStepCallMapper = BusinessToFileStepCallMapper(
            businessToFilePhaseMapper = businessToFilePhaseMapper
    )

    private val businessToFileStepMapper = BusinessToFileStepMapper(
            businessToFilePhaseMapper = businessToFilePhaseMapper,
            businessToFileStepCallMapper = businessToFileStepCallMapper
    )

    private val businessToFileTestMapper = BusinessToFileTestMapper(
            businessToFileStepCallMapper = businessToFileStepCallMapper
    )

    private val businessToFileFeatureMapper = BusinessToFileFeatureMapper()

    private val businessToFileTestPlanMapper = BusinessToFileManualTestPlanMapper()

    private val businessToFileManualStepStatusMapper = BusinessToFileManualStepStatusMapper()

    private val businessToFileManualTestStatusMapper = BusinessToFileManualTestStatusMapper()

    private val businessToFileManualStepCallMapper = BusinessToFileManualStepCallMapper(
            stepCallMapper = businessToFileStepCallMapper,
            stepStatusMapper = businessToFileManualStepStatusMapper
    )

    private val businessToFileManualTestMapper = BusinessToFileManualTestMapper(
            manualStepCallMapper = businessToFileManualStepCallMapper,
            manualTestStatusMapper = businessToFileManualTestStatusMapper
    )


    //---------------------------------------- services --------------------------------------------------------//

    val resourceFileService = ResourceFileService()

    private val composedStepFileService = ComposedStepFileService(
            fileToBusinessStepMapper = fileToBusinessStepMapper,
            businessToFileStepMapper = businessToFileStepMapper
    )

    private val testsFileService = TestFileService(
            fileToBusinessTestMapper = fileToBusinessTestMapper,
            businessToFileTestMapper = businessToFileTestMapper
    )

    val featuresFileService = FeatureFileService(
            featureMapper = fileToBusinessFeatureMapper,
            businessToFileFeatureMapper = businessToFileFeatureMapper
    )

    val manualTestPlanFileService = ManualTestPlanFileService(
            businessToFileManualTestPlanMapper = businessToFileTestPlanMapper,
            fileToBusinessManualTestPlanMapper = fileToBusinessManualTestPlanMapper
    )

    val manualTestFileService = ManualTestFileService(
            businessToFileManualTestMapper = businessToFileManualTestMapper,
            fileToBusinessManualTestMapper = fileToBusinessManualTestMapper
    )

    val testerumProjectFileService = TesterumProjectFileService()

    val recentProjectsFileService = RecentProjectsFileService(
            testerumProjectFileService = testerumProjectFileService
    )

    val variablesFileService = VariablesFileService()

    val settingsFileService = SettingsFileService()

    val runnerResultFileService = ResultsFileService()

    val warningService = WarningService()


    //---------------------------------------- caches & resolving ----------------------------------------------//

    private val argsResolver = ArgsResolver(
            resourceFileService = resourceFileService
    )

    private val stepsResolver = StepsResolver(
            argsResolver = argsResolver
    )

    val stepsCache = StepsCache(
            persistentCacheManger = scannerModuleFactory.stepLibraryPersistentCacheManger,
            settingsManager = settingsModuleFactory.settingsManager,
            composedStepFileService = composedStepFileService,
            stepsResolver = stepsResolver,
            warningService = warningService
    )

    val testResolver = TestResolver(
            stepsCache = stepsCache,
            argsResolver = argsResolver
    )

    val testsCache = TestsCache(
            testFileService = testsFileService,
            testResolver = testResolver,
            warningService = warningService
    )

    val featuresCache = FeaturesCache(
            featureFileService = featuresFileService
    )

    val recentProjectsCache = RecentProjectsCache(
            recentProjectsFileService = recentProjectsFileService,
            testerumProjectFileService = testerumProjectFileService
    )

}
