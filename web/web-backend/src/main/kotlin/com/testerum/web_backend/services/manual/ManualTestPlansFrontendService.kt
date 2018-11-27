package com.testerum.web_backend.services.manual

import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.file.ManualTestFileService
import com.testerum.file_service.file.ManualTestPlanFileService
import com.testerum.model.exception.ValidationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.ManualTestPlan
import com.testerum.model.manual.ManualTestPlans
import com.testerum.model.manual.ManualTreeTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.status_tree.ManualTestsStatusTreeRoot
import com.testerum.model.manual.status_tree.builder.ManualTestsTreeBuilder
import com.testerum.model.manual.status_tree.filter.ManualTreeStatusFilter
import com.testerum.test_file_format.manual_test_plan.FileManualTestPlan
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.manual.filterer.ManualTestsTreeFilterer
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class ManualTestPlansFrontendService(private val testsCache: TestsCache,
                                     private val automatedToManualTestMapper: AutomatedToManualTestMapper,
                                     private val frontendDirs: FrontendDirs,
                                     private val manualTestPlanFileService: ManualTestPlanFileService,
                                     private val manualTestFileService: ManualTestFileService,
                                     private val testResolver: TestResolver) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManualTestPlansFrontendService::class.java)
    }

    fun savePlan(plan: ManualTestPlan): ManualTestPlan {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val planToSave: ManualTestPlan

        val existingPlan = getPlanAtPath(plan.path)
        if (existingPlan == null) {
            planToSave = plan
        } else {
            // delete tests
            val testPathsWithManualTestExtension = plan.manualTreeTests.map {
                it.path.copy(fileExtension = ManualTest.MANUAL_TEST_FILE_EXTENSION)
            }
            val existingTestPaths = existingPlan.manualTreeTests.map { it.path }
            val pathsOfTestsToDelete = existingTestPaths.filter { it !in testPathsWithManualTestExtension }

            for (path in pathsOfTestsToDelete) {
                manualTestFileService.deleteTestAtPath(path, plan.path, manualTestsDir)
            }

            // ignore common tests; when updating a plan, we don't update the tests, we merely create/delete (select/deselect)
            planToSave = plan.copy(
                    manualTreeTests = plan.manualTreeTests.filter {
                        it.path.copy(fileExtension = ManualTest.MANUAL_TEST_FILE_EXTENSION) !in existingTestPaths
                    }
            )
        }

        // save plan
        val savedTestPlan = manualTestPlanFileService.save(planToSave, manualTestsDir)

        // save tests
        val pathsToSave = planToSave.manualTreeTests.map { it.path }

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
        val resourcesDir = frontendDirs.getRequiredResourcesDir()

        val plans = manualTestPlanFileService.getPlans(manualTestsDir)

        val activeTestPlans = plans.activeTestPlans.map { loadPlanTests(it, manualTestsDir, resourcesDir) }
        val finalizedTestPlans = plans.finalizedTestPlans.map { loadPlanTests(it, manualTestsDir, resourcesDir) }

        return plans.copy(
                activeTestPlans = activeTestPlans,
                finalizedTestPlans = finalizedTestPlans
        )
    }

    fun getPlanAtPath(planPath: Path): ManualTestPlan? {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()
        val resourcesDir = frontendDirs.getRequiredResourcesDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return null

        return loadPlanTests(plan, manualTestsDir, resourcesDir)
    }

    private fun loadPlanTests(plan: ManualTestPlan,
                              manualTestsDir: JavaPath,
                              resourcesDir: JavaPath): ManualTestPlan {
        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val resolvedTests = tests.map { testResolver.resolveManualStepDefs(it, resourcesDir) }
        val testsWithFinalizedFlag = resolvedTests.map { it.copy(isFinalized = plan.isFinalized) }

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
                manualTreeTests = manualTreeTests.sortedBy { it.path.withoutFileExtension().toString().toLowerCase() },
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
        val resourcesDir = frontendDirs.getRequiredResourcesDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return ManualTestsStatusTreeRoot.EMPTY

        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val resolvedTests = tests.map { testResolver.resolveManualStepDefs(it, resourcesDir) }
        val testsWithFinalizedFlag = resolvedTests.map { it.copy(isFinalized = plan.isFinalized) }

        val treeBuilder = ManualTestsTreeBuilder(testPlanName = plan.name)

        val filteredTests = ManualTestsTreeFilterer.filterTests(testsWithFinalizedFlag, filter)

        for (test in filteredTests) {
            treeBuilder.addTest(test)
        }

        return treeBuilder.build()
    }

    fun getTestAtPath(planPath: Path,
                      testPath: Path): ManualTest? {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()
        val resourcesDir = frontendDirs.getRequiredResourcesDir()

        val test: ManualTest = manualTestFileService.getTestAtPath(planPath, testPath, manualTestsDir)
                ?: return null
        val resolvedTest = testResolver.resolveManualStepDefs(test, resourcesDir)

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
        val isFinalized = plan?.isFinalized ?: FileManualTestPlan.IS_FINALIZED_DEFAULT

        return resolvedTest.copy(isFinalized = isFinalized)
    }

    fun finalizePlan(planPath: Path): ManualTestPlan {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException("The test plan at path [$planPath] does not exist")

        val finalizedPlan = plan.copy(
                isFinalized = true,
                finalizedDate = LocalDateTime.now()
        )

        manualTestPlanFileService.save(finalizedPlan, manualTestsDir)

        return getPlanAtPath(planPath)!!
    }

    fun makePlanActive(planPath: Path): ManualTestPlan {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException("The test plan at path [$planPath] does not exist")

        val activePlan = plan.copy(
                isFinalized = false,
                finalizedDate = null
        )

        manualTestPlanFileService.save(activePlan, manualTestsDir)

        return getPlanAtPath(planPath)!!
    }

    fun updateTest(planPath: Path, test: ManualTest): ManualTest {
        val manualTestsDir = frontendDirs.getRequiredManualTestsDir()

        return manualTestFileService.save(test, planPath, manualTestsDir)
    }

    fun getNextTestToExecute(planPath: Path,
                             currentTestPath: Path): Path? {
        val plan: ManualTestPlan = getPlanAtPath(planPath)
                ?: return null

        val testPaths = plan.manualTreeTests.map { it.path }
        if (testPaths.isEmpty()) {
            return null
        }

        val indexOfCurrentTestPath = testPaths.indexOf(currentTestPath)
        if (indexOfCurrentTestPath == -1) {
            return testPaths[0]
        } else if (indexOfCurrentTestPath == testPaths.size - 1) {
            return testPaths[0]
        } else {
            return testPaths[indexOfCurrentTestPath + 1]
        }
    }

}
