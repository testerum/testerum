package com.testerum.web_backend.services.manual

import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.file.ManualTestFileService
import com.testerum.file_service.file.ManualTestPlanFileService
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualStepCall
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.ManualTestPlan
import com.testerum.model.manual.ManualTestPlans
import com.testerum.model.manual.ManualTreeTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.manual.status_tree.builder.ManualTestPlanTreeBuilder
import com.testerum.model.manual.status_tree.filter.ManualTreeStatusFilter
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class ManualTestPlansFrontendService(private val testsCache: TestsCache,
                                     private val automatedToManualTestMapper: AutomatedToManualTestMapper,
                                     private val frontendDirs: FrontendDirs,
                                     private val manualTestPlanFileService: ManualTestPlanFileService,
                                     private val manualTestFileService: ManualTestFileService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManualTestPlansFrontendService::class.java)
    }

    fun savePlan(plan: ManualTestPlan): ManualTestPlan {
        // todo: check if the directory already exists, and update if needed

        // save test plan
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()
        val savedTestPlan = manualTestPlanFileService.save(plan, manualTestsDir)


        val pathsToSave = plan.manualTreeTests.map { it.path }

        for (pathToSave in pathsToSave) {
            val test = testsCache.getTestAtPath(pathToSave)

            if (test == null) {
                LOG.warn("path [$pathToSave] will not be part of the new test plan, because there is no test at that path")
                continue
            }

            val manualTest = automatedToManualTestMapper.convert(test)

            manualTestFileService.save(
                    manualTest,
                    savedTestPlan.path,
                    manualTestsDir
            )
        }

        return getPlanAtPath(savedTestPlan.path)!!
    }

    fun getPlans(): ManualTestPlans {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plans = manualTestPlanFileService.getPlans(manualTestsDir)

        val activeTestPlans = plans.activeTestPlans.map { loadPlanTests(it, manualTestsDir) }
        val finalizedTestPlans = plans.finalizedTestPlans.map { loadPlanTests(it, manualTestsDir) }

        return plans.copy(
                activeTestPlans = activeTestPlans,
                finalizedTestPlans = finalizedTestPlans
        )
    }

    fun getPlanAtPath(planPath: Path): ManualTestPlan? {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return null

        return loadPlanTests(plan, manualTestsDir)
    }

    private fun loadPlanTests(plan: ManualTestPlan, manualTestsDir: JavaPath): ManualTestPlan {
        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val testsWithFinalizedFlag = tests.map { it.copy(isFinalized = plan.isFinalized) }

        val manualTreeTests = testsWithFinalizedFlag.map {
            ManualTreeTest(
                    path = it.path,
                    testName = it.name
            )
        }

        val passedTests = testsWithFinalizedFlag.count { it.status == ManualTestStatus.PASSED }
        val failedTests = testsWithFinalizedFlag.count { it.status == ManualTestStatus.FAILED }
        val blockedTests = testsWithFinalizedFlag.count { it.status == ManualTestStatus.BLOCKED }
        val notApplicableTests = testsWithFinalizedFlag.count { it.status == ManualTestStatus.NOT_APPLICABLE }
        val notExecutedOrInProgressTests = testsWithFinalizedFlag.count { it.status == ManualTestStatus.NOT_EXECUTED || it.status == ManualTestStatus.IN_PROGRESS }

        return plan.copy(
                manualTreeTests = manualTreeTests,
                passedTests = passedTests,
                failedTests = failedTests,
                blockedTests = blockedTests,
                notApplicableTests = notApplicableTests,
                notExecutedOrInProgressTests = notExecutedOrInProgressTests
        )
    }

    fun deletePlanAtPath(planPath: Path) {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        manualTestPlanFileService.deleteAtPath(planPath, manualTestsDir)
    }

    fun getTestsTreeAtPlanPath(planPath: Path,
                               filter: ManualTreeStatusFilter): ManualTestsStatusTreeRoot {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return ManualTestsStatusTreeRoot.EMPTY

        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val testsWithFinalizedFlag = tests.map { it.copy(isFinalized = plan.isFinalized) }

        val treeBuilder = ManualTestPlanTreeBuilder(testPlanName = plan.name)

        // todo: filter
        val filteredTests = testsWithFinalizedFlag

        for (test in filteredTests) {
            treeBuilder.addTest(test)
        }

        return treeBuilder.build()
    }

    fun getTestAtPath(planPath: Path,
                      testPath: Path): ManualTest? {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        // todo: read is-finalized from containing plan

        return manualTestFileService.getTestAtPath(planPath, testPath, manualTestsDir)
    }

    fun finalizePlan(planPath: Path): ManualTestPlan {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException("The test plan at path [$planPath] does not exist")

        val finalizedPlan = plan.copy(
                isFinalized = true,
                finalizedDate = LocalDateTime.now()
        )

        return savePlan(finalizedPlan)
    }

    fun makePlanActive(planPath: Path): ManualTestPlan {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException("The test plan at path [$planPath] does not exist")

        val activePlan = plan.copy(
                isFinalized = false,
                finalizedDate = null
        )

        return savePlan(activePlan)
    }

    fun updateTest(planPath: Path, test: ManualTest): ManualTest {
        return ManualTest(
                test.path,
                test.path,
                "Sign-up",
                "This is a positive test for the sign-up functionality.",
                listOf("sign-up", "user"),
                listOf(
                        ManualStepCall(
                                stepCall = StepCall(
                                        "1",
                                        ComposedStepDef(
                                                Path.createInstance("signup"),
                                                Path.createInstance("signup"),
                                                StepPhaseEnum.GIVEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I'm on the sign-up page")
                                                        )
                                                ),
                                                "",
                                                listOf("tag1", "tag2"),
                                                listOf()
                                        ),
                                        emptyList()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "2",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I write the email address 'test@testerum.com'")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "3",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I write the password 'myPassword123'")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "4",
                                        BasicStepDef(
                                                StepPhaseEnum.WHEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("I press the SignUp button")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        ),
                        ManualStepCall(
                                stepCall = StepCall(
                                        "5",
                                        BasicStepDef(
                                                StepPhaseEnum.THEN,
                                                StepPattern(
                                                        listOf(
                                                                TextStepPatternPart("A user is created in the database")
                                                        )
                                                ),
                                                className = "com.testerum.ClasaDePoveste",
                                                methodName = "zmeulCelRau"
                                        ),
                                        listOf()
                                ),
                                status = ManualTestStepStatus.NOT_EXECUTED
                        )
                ),
                ManualTestStatus.NOT_EXECUTED,
                "",
                false
        )
    }

}
