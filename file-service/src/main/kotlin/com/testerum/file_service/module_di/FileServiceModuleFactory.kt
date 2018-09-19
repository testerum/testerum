package com.testerum.file_service.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.caches.resolved.resolvers.ArgsResolver
import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.*
import com.testerum.file_service.mapper.business_to_file.BusinessToFileFeatureMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileStepMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileTestMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFilePhaseMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessFeatureMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessStepMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessTestMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessPhaseMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
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

    val variablesFileService = VariablesFileService()

    val settingsFileService = SettingsFileService()

    val runnerResultFileService = RunnerResultFileService()

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

    private val testResolver = TestResolver(
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

}
