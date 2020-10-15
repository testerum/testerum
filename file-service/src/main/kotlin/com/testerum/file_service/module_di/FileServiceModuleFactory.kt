package com.testerum.file_service.module_di

import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_service.business.trial.TrialService
import com.testerum.file_service.caches.resolved.resolvers.ArgsResolver
import com.testerum.file_service.caches.resolved.resolvers.StepsResolver
import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.ComposedStepFileService
import com.testerum.file_service.file.FeatureFileService
import com.testerum.file_service.file.LocalVariablesFileService
import com.testerum.file_service.file.ManualTestFileService
import com.testerum.file_service.file.ManualTestPlanFileService
import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.ResourceFileService
import com.testerum.file_service.file.ResultsFileService
import com.testerum.file_service.file.RunConfigFileService
import com.testerum.file_service.file.SeleniumDriversFileService
import com.testerum.file_service.file.SettingsFileService
import com.testerum.file_service.file.TestFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.file_service.file.VariablesFileService
import com.testerum.file_service.file.trial.JavaPreferencesTrialFileService
import com.testerum.file_service.file.trial.TrialFileService
import com.testerum.file_service.mapper.business_to_file.BusinessToFileFeatureMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileRunConfigMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioMapper
import com.testerum.file_service.mapper.business_to_file.BusinessToFileScenarioParamMapper
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
import com.testerum.file_service.mapper.file_to_business.FileToBusinessRunConfigMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessScenarioMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessScenarioParamMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessStepMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessTestMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessPhaseMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualStepCallMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualStepStatusMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestPlanMapper
import com.testerum.file_service.mapper.file_to_business.manual.FileToBusinessManualTestStatusMapper
import com.testerum.settings.module_di.SettingsModuleFactory
import java.time.Clock

class FileServiceModuleFactory(context: ModuleFactoryContext,
                               settingsModuleFactory: SettingsModuleFactory) : BaseModuleFactory(context) {

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

    val testResolver = TestResolver(
            argsResolver = argsResolver
    )

}
