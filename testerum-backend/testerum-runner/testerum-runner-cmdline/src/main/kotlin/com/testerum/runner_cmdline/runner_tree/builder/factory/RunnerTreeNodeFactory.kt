package com.testerum.runner_cmdline.runner_tree.builder.factory

import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.HookPhase
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.new_tree_builder.ContainerTreeNode
import com.testerum.model.util.new_tree_builder.TreeNode
import com.testerum.model.util.new_tree_builder.TreeNodeFactory
import com.testerum.runner_cmdline.runner_tree.builder.TestWithFilePath
import com.testerum.runner_cmdline.runner_tree.builder.factory.impl.RunnerFeatureNodeFactory
import com.testerum.runner_cmdline.runner_tree.builder.factory.impl.RunnerParametrizedTestNodeFactory
import com.testerum.runner_cmdline.runner_tree.builder.factory.impl.RunnerSuiteNodeFactory
import com.testerum.runner_cmdline.runner_tree.builder.factory.impl.RunnerTestNodeFactory
import com.testerum.runner_cmdline.runner_tree.nodes.RunnerTreeNode
import com.testerum.runner_cmdline.runner_tree.nodes.feature.RunnerFeature
import com.testerum.runner_cmdline.runner_tree.nodes.hook.RunnerBasicHook
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef

class RunnerTreeNodeFactory(
    hooks: Collection<HookDef>,
    private val executionName: String?,
    private val glueClassNames: List<String>
) : TreeNodeFactory<RunnerSuite, RunnerFeature> {

    private val beforeEachTestBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.BEFORE_EACH_TEST)
    private val afterEachTestBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.AFTER_EACH_TEST)
    private val beforeAllTestsBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.BEFORE_ALL_TESTS)
    private val afterAllTestsBasicHooks: List<RunnerBasicHook> = hooks.sortedBasicHooksForPhase(HookPhase.AFTER_ALL_TESTS)

    override fun createRootNode(item: HasPath?): RunnerSuite {
        return RunnerSuiteNodeFactory.create(
            item,
            executionName,
            glueClassNames,
            beforeAllTestsBasicHooks,
            afterAllTestsBasicHooks
        )
    }

    override fun createVirtualContainer(parentNode: ContainerTreeNode, path: Path): RunnerFeature {
        return RunnerFeature(
            parent = parentNode,
            indexInParent = parentNode.childrenCount,
            featurePathFromRoot = path.directories,
            featureName = path.directories.last(),
            tags = emptyList(),
            feature = Feature(
                name = "",
                path = Path.EMPTY,
            )
        )
    }

    override fun createNode(parentNode: ContainerTreeNode, item: HasPath): TreeNode {
        if (parentNode !is RunnerTreeNode) {
            throw IllegalStateException("unexpected parent type: [${parentNode.javaClass}]: [$parentNode]")
        }
        val indexInParent = parentNode.childrenCount

        return when (item) {
            is Feature -> RunnerFeatureNodeFactory.create(parentNode, item)
            is TestWithFilePath -> {
                val isParametrizedTest = item.test.scenarios.isNotEmpty()

                if (isParametrizedTest) {
                    RunnerParametrizedTestNodeFactory.create(item, parentNode, indexInParent, beforeEachTestBasicHooks, afterEachTestBasicHooks)
                } else {
                    RunnerTestNodeFactory.create(item, parentNode, indexInParent, beforeEachTestBasicHooks, afterEachTestBasicHooks)
                }
            }
            else -> throw IllegalArgumentException("unexpected item type [${item.javaClass}]: [$item]")
        }
    }

    private fun Collection<HookDef>.sortedBasicHooksForPhase(phase: HookPhase): List<RunnerBasicHook> {
        return this.asSequence()
            .filter { it.phase == phase }
            .sortedBy { it.order }
            .map { RunnerBasicHook(it) }
            .toList()
    }
}
