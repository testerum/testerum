package com.testerum.web_backend.services.manual

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
import com.testerum.web_backend.services.manual.filterer.ManualTestsTreeFilterer
import com.testerum.web_backend.services.project.WebProjectManager
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class ManualTestPlansFrontendService(private val webProjectManager: WebProjectManager,
                                     private val automatedToManualTestMapper: AutomatedToManualTestMapper,
                                     private val manualTestPlanFileService: ManualTestPlanFileService,
                                     private val manualTestFileService: ManualTestFileService,
                                     private val testResolver: TestResolver) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ManualTestPlansFrontendService::class.java)
    }

    private fun getManualTestsDir() = webProjectManager.getProjectServices().dirs().getManualTestsDir()

    private fun getResourcesDir() = webProjectManager.getProjectServices().dirs().getResourcesDir()

    fun savePlan(plan: ManualTestPlan): ManualTestPlan {
        val manualTestsDir = getManualTestsDir()

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
            val test = webProjectManager.getProjectServices().getTestsCache().getTestAtPath(pathToSave)

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
        val manualTestsDir = getManualTestsDir()
        val resourcesDir = getResourcesDir()

        val plans = manualTestPlanFileService.getPlans(manualTestsDir)

        val activeTestPlans = plans.activeTestPlans.map { loadPlanTests(it, manualTestsDir, resourcesDir) }
        val finalizedTestPlans = plans.finalizedTestPlans.map { loadPlanTests(it, manualTestsDir, resourcesDir) }

        return plans.copy(
                activeTestPlans = activeTestPlans,
                finalizedTestPlans = finalizedTestPlans
        )
    }

    fun getPlanAtPath(planPath: Path): ManualTestPlan? {
        val manualTestsDir = getManualTestsDir()
        val resourcesDir = getResourcesDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return null

        return loadPlanTests(plan, manualTestsDir, resourcesDir)
    }

    private fun loadPlanTests(plan: ManualTestPlan,
                              manualTestsDir: JavaPath,
                              resourcesDir: JavaPath): ManualTestPlan {
        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val resolvedTests = tests.map { testResolver.resolveManualStepDefs({webProjectManager.getProjectServices().getStepsCache()}, it, resourcesDir) }
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
        val manualTestsDir = getManualTestsDir()

        manualTestPlanFileService.deleteAtPath(planPath, manualTestsDir)
    }

    fun getTestsTreeAtPlanPath(planPath: Path,
                               filter: ManualTreeStatusFilter): ManualTestsStatusTreeRoot {
        val manualTestsDir = getManualTestsDir()
        val resourcesDir = getResourcesDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: return ManualTestsStatusTreeRoot.EMPTY

        val tests = manualTestFileService.getTestsAtPlanPath(plan.path, manualTestsDir)
        val resolvedTests = tests.map { testResolver.resolveManualStepDefs({webProjectManager.getProjectServices().getStepsCache()}, it, resourcesDir) }
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
        val manualTestsDir = getManualTestsDir()
        val resourcesDir = getResourcesDir()

        val test: ManualTest = manualTestFileService.getTestAtPath(planPath, testPath, manualTestsDir)
                ?: return null
        val resolvedTest = testResolver.resolveManualStepDefs({webProjectManager.getProjectServices().getStepsCache()}, test, resourcesDir)

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
        val isFinalized = plan?.isFinalized ?: FileManualTestPlan.IS_FINALIZED_DEFAULT

        return resolvedTest.copy(isFinalized = isFinalized)
    }

    fun finalizePlan(planPath: Path): ManualTestPlan {
        val manualTestsDir = getManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException(
                        globalMessage = "The test plan at path [$planPath] does not exist",
                        globalHtmlMessage = "The test plan at path<br/><code>$planPath</code><br/>does not exist"
                )

        val finalizedPlan = plan.copy(
                isFinalized = true,
                finalizedDate = LocalDateTime.now()
        )

        manualTestPlanFileService.save(finalizedPlan, manualTestsDir)

        return getPlanAtPath(planPath)!!
    }

    fun makePlanActive(planPath: Path): ManualTestPlan {
        val manualTestsDir = getManualTestsDir()

        val plan = manualTestPlanFileService.getPlanAtPath(planPath, manualTestsDir)
                ?: throw ValidationException(
                        globalMessage = "The test plan at path [$planPath] does not exist",
                        globalHtmlMessage = "The test plan at path<br/><code>$planPath</code><br/>does not exist"
                )

        val activePlan = plan.copy(
                isFinalized = false,
                finalizedDate = null
        )

        manualTestPlanFileService.save(activePlan, manualTestsDir)

        return getPlanAtPath(planPath)!!
    }

    fun updateTest(planPath: Path, test: ManualTest): ManualTest {
        val manualTestsDir = getManualTestsDir()

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
