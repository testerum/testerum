package com.testerum.file_service.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.business.trial.TrialService
import com.testerum.file_service.caches.resolved.BasicStepsCache
import com.testerum.file_service.caches.resolved.resolvers.ArgsResolver
import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.*
import com.testerum.file_service.file.trial.JavaPreferencesTrialFileService
import com.testerum.file_service.file.trial.TrialFileService
import com.testerum.file_service.mapper.business_to_file.*
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFilePhaseMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.file_service.mapper.business_to_file.manual.*
import com.testerum.file_service.mapper.file_to_business.*
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessPhaseMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.file_service.mapper.file_to_business.manual.*
import com.testerum.scanner.step_lib_scanner.module_di.TesterumScannerModuleFactory
import com.testerum.settings.module_di.SettingsModuleFactory
import java.time.Clock

class FileServiceModuleFactory(context: ModuleFactoryContext,
                               settingsModuleFactory: SettingsModuleFactory,
                               scannerModuleFactory: TesterumScannerModuleFactory) : BaseModuleFactory(context) {

    //---------------------------------------- mapper: file -> business ----------------------------------------------//

    private val fileToBusinessStepPhaseMapper = FileToBusinessPhaseMapper()

    private val fileToBusinessStepCallMapper = FileToBusinessStepCallMapper(
            phaseMapper = fileToBusinessStepPhaseMapper
    )

    private val fileToBusinessStepMapper = FileToBusinessStepMapper(
            phaseMapper = fileToBusinessStepPhaseMapper,
            callsMapper = fileToBusinessStepCallMapper
    )

    private val fileToBusinessScenarioParamMapper = FileToBusinessScenarioParamMapper()

    private val fileToBusinessScenarioMapper = FileToBusinessScenarioMapper(
            fileToBusinessScenarioParamMapper = fileToBusinessScenarioParamMapper
    )

    private val fileToBusinessTestMapper = FileToBusinessTestMapper(
            stepCallMapper = fileToBusinessStepCallMapper,
            fileToBusinessScenarioMapper = fileToBusinessScenarioMapper
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

    private val businessToFileRunConfigMapper = BusinessToFileRunConfigMapper()

    private val fileToBusinessRunConfigMapper = FileToBusinessRunConfigMapper()


    //---------------------------------------- mapper: business -> file ----------------------------------------------//

    private val businessToFilePhaseMapper = BusinessToFilePhaseMapper()

    private val businessToFileStepCallMapper = BusinessToFileStepCallMapper(
            businessToFilePhaseMapper = businessToFilePhaseMapper
    )

    private val businessToFileStepMapper = BusinessToFileStepMapper(
            businessToFilePhaseMapper = businessToFilePhaseMapper,
            businessToFileStepCallMapper = businessToFileStepCallMapper
    )

    private val businessToFileScenarioParamMapper = BusinessToFileScenarioParamMapper()

    private val businessToFileScenarioMapper = BusinessToFileScenarioMapper(
            businessToFileScenarioParamMapper = businessToFileScenarioParamMapper
    )

    private val businessToFileTestMapper = BusinessToFileTestMapper(
            businessToFileScenarioMapper = businessToFileScenarioMapper,
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


    //---------------------------------------- file services ---------------------------------------------------------//

    val resourceFileService = ResourceFileService()

    val composedStepFileService = ComposedStepFileService(
            fileToBusinessStepMapper = fileToBusinessStepMapper,
            businessToFileStepMapper = businessToFileStepMapper
    )

    val testsFileService = TestFileService(
            fileToBusinessTestMapper = fileToBusinessTestMapper,
            businessToFileTestMapper = businessToFileTestMapper
    )

    val featuresFileService = FeatureFileService(
            featureMapper = fileToBusinessFeatureMapper,
            businessToFileFeatureMapper = businessToFileFeatureMapper
    )

    val runConfigFileService = RunConfigFileService(
            fileToBusinessMapper = fileToBusinessRunConfigMapper,
            businessToFileMapper = businessToFileRunConfigMapper
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
            testerumDirs = settingsModuleFactory.testerumDirs
    )

    val localVariablesFileService = LocalVariablesFileService()

    val variablesFileService = VariablesFileService(
            localVariablesFileService = localVariablesFileService
    )

    val settingsFileService = SettingsFileService()

    val runnerResultFileService = ResultsFileService()

    val warningService = WarningService()

    private val trialFileService: TrialFileService = JavaPreferencesTrialFileService()

    val seleniumDriversFileService = SeleniumDriversFileService()


    //---------------------------------------- business services -----------------------------------------------------//

    val trialService = TrialService(
            trialFileService = trialFileService,
            clock = Clock.systemDefaultZone()
    )


    //---------------------------------------- caches & resolving ----------------------------------------------------//

    private val argsResolver = ArgsResolver(
            resourceFileService = resourceFileService
    )

    val stepsResolver = StepsResolver(
            argsResolver = argsResolver
    )

    val basicStepsCache = BasicStepsCache(
            persistentCacheManager = scannerModuleFactory.extensionsScanner,
            settingsManager = settingsModuleFactory.settingsManager
    )

    val testResolver = TestResolver(
            argsResolver = argsResolver
    )

}
