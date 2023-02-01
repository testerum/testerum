package com.testerum.runner_cmdline.runner_tree.builder.factory.impl

import com.testerum.model.test.scenario.Scenario
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.runner_cmdline.runner_tree.builder.TestWithFilePath
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerParametrizedTest
import com.testerum.runner_cmdline.runner_tree.nodes.parametrized_test.RunnerScenario
import org.slf4j.LoggerFactory

object RunnerParametrizedTestNodeFactory {

    private val LOG = LoggerFactory.getLogger(RunnerParametrizedTestNodeFactory::class.java)

    fun create(
        item: TestWithFilePath,
        parentNode: RunnerTreeNode,
        indexInParent: Int,
        beforeEachTestBasicHooks: List<RunnerBasicHook>,
        afterEachTestBasicHooks: List<RunnerBasicHook>
    ): RunnerParametrizedTest {
        val runnerParametrizedTest = RunnerParametrizedTest(
            parent = parentNode,
            test = item.test,
            filePath = item.testPath.javaPath,
            indexInParent = indexInParent,
        )

        verifyScenarioIndexesFromFilter(item)

        val filteredTestScenarios = filterScenarios(item)

        val runnerScenariosNodes: List<RunnerScenario> = filteredTestScenarios.mapIndexed { filteredScenarioIndex, scenarioWithOriginalIndex ->
            RunnerScenarioNodeFactory.create(
                parentNode = runnerParametrizedTest,
                test = item.test,
                filePath = item.testPath.javaPath,
                scenarioWithOriginalIndex = scenarioWithOriginalIndex,
                filteredScenarioIndex = filteredScenarioIndex,
                beforeEachTestBasicHooks = beforeEachTestBasicHooks,
                afterEachTestBasicHooks = afterEachTestBasicHooks
            )
        }

        runnerParametrizedTest.scenarios = runnerScenariosNodes
        return runnerParametrizedTest
    }

    private fun verifyScenarioIndexesFromFilter(item: TestWithFilePath) {
        if (item.testPath !is ScenariosTestPath) {
            return
        }

        for (scenarioIndex in item.testPath.scenarioIndexes) {
            if ((scenarioIndex < 0) || (scenarioIndex >= item.test.scenarios.size)) {
                LOG.warn(
                    "invalid scenario index [$scenarioIndex]" +
                        " for test at [${item.testPath.testFile}]" +
                        ": this test has only ${item.test.scenarios.size} scenarios" +
                        "; the index must be between 0 and ${item.test.scenarios.size - 1} inclusive"
                )
            }
        }
    }

    private fun filterScenarios(item: TestWithFilePath): List<Pair<Int, Scenario>> {
        val scenariosWithOriginalIndex = item.test.scenarios.mapIndexed { index, scenario ->
            index to scenario
        }

        val filteredTestScenarios = if (item.testPath is ScenariosTestPath) {
            // filter scenarios
            if (item.testPath.scenarioIndexes.isEmpty()) {
                // there is no filter on scenarios
                scenariosWithOriginalIndex
            } else {
                scenariosWithOriginalIndex.filterIndexed { scenarioIndex, _ ->
                    scenarioIndex in item.testPath.scenarioIndexes
                }
            }
        } else {
            scenariosWithOriginalIndex
        }

        return filteredTestScenarios
    }
}
