package com.testerum.runner_cmdline.runner_tree

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.BasicStepDef
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.tests_finder.TestsFinder
import com.testerum.model.util.new_tree_builder.PathBasedTreeBuilder
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.project_manager.RunnerProjectManager
import com.testerum.runner_cmdline.runner_tree.builder.TestWithFilePath
import com.testerum.runner_cmdline.runner_tree.builder.factory.RunnerTreeNodeFactory
import com.testerum.runner_cmdline.runner_tree.nodes.suite.RunnerSuite
import com.testerum.scanner.step_lib_scanner.model.hooks.HookDef
import java.nio.file.Path as JavaPath

class RunnerExecutionTreeBuilder(
    private val runnerProjectManager: RunnerProjectManager,
    private val executionName: String?
) {

    private val stepsCache: StepsCache = runnerProjectManager.getProjectServices().getStepsCache()
    private val featureCache: FeaturesCache = runnerProjectManager.getProjectServices().getFeatureCache()

    fun createTree(
        cmdlineParams: RunCmdlineParams,
        testsDir: JavaPath
    ): RunnerSuite {
        val testsDirectoryRoot = testsDir.toAbsolutePath()
        val testsMap = TestsFinder.loadTestsToRun(
            testPaths = cmdlineParams.testPaths,
            tagsToInclude = cmdlineParams.includeTags,
            tagsToExclude = cmdlineParams.excludeTags,
            testsDirectoryRoot = testsDirectoryRoot,
            loadTestAtPath = { runnerProjectManager.getProjectServices().getTestsCache().getTestAtPath(it) }
        )
        val tests = testsMap.map { (path, test) -> TestWithFilePath(test, path) }
        val features = loadFeatures(tests.map { it.testPath.javaPath }, testsDirectoryRoot)

        val items = mutableListOf<HasPath>()
        items += tests
        items += features

        val hooks: Collection<HookDef> = stepsCache.getHooks()
        val glueClassNames = getGlueClassNames(hooks, tests, features)
        val treeBuilder = PathBasedTreeBuilder(
            RunnerTreeNodeFactory(hooks, executionName, glueClassNames)
        )

        return treeBuilder.createTree(items)
    }

    private fun loadFeatures(testJavaPaths: List<JavaPath>, testsDirectoryRoot: JavaPath): List<Feature> {
        val result = arrayListOf<Feature>()

        val possibleFeaturePaths = getPossibleFeaturePaths(testJavaPaths, testsDirectoryRoot)
        for (possibleFeaturePath in possibleFeaturePaths) {
            val feature = featureCache.getFeatureAtPath(possibleFeaturePath)
                ?: continue

            result += feature
        }

        // add virtual root feature if it doesn't exist (e.g. to get an empty list of hooks)
        val rootFeatureNotFound = result.none { it.path == Path.EMPTY }
        if (rootFeatureNotFound) {
            result += Feature(
                name = "",
                path = Path.EMPTY,
            )
        }

        return result
    }

    private fun getPossibleFeaturePaths(testJavaPaths: List<JavaPath>, testsDirectoryRoot: JavaPath): List<Path> {
        val result = linkedSetOf<String>()

        for (testJavaPath in testJavaPaths) {
            val relativeTestJavaPath = testsDirectoryRoot.relativize(testJavaPath)
            val testPath = Path.createInstance(relativeTestJavaPath.toString())

            val testPathDirectories = testPath.directories
            for (i in 1..testPathDirectories.size) {
                result += testPathDirectories.subList(0, i)
                    .joinToString(separator = "/")
            }
        }

        // add root feature
        // we need to load it because it may have hooks
        result += ""

        return result.map {
            Path.createInstance(it)
        }
    }

    private fun getGlueClassNames(
        basicHookDefs: Collection<HookDef>,
        testsWithPaths: List<TestWithFilePath>,
        features: List<Feature>
    ): List<String> {
        val result = mutableListOf<String>()

        for (basicHookDef in basicHookDefs) {
            result += basicHookDef.className
        }

        for (testWithPath in testsWithPaths) {
            val test = testWithPath.test

            for (stepCall in test.afterHooks) {
                addGlueClassNames(result, stepCall)
            }

            for (stepCall in test.stepCalls) {
                addGlueClassNames(result, stepCall)
            }
        }

        for (feature in features) {
            for (stepCall in feature.hooks.beforeAll) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.beforeEach) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.afterEach) {
                addGlueClassNames(result, stepCall)
            }
            for (stepCall in feature.hooks.afterAll) {
                addGlueClassNames(result, stepCall)
            }
        }

        return result
    }

    private fun addGlueClassNames(
        result: MutableList<String>,
        stepCall: StepCall
    ) {
        when (val stepDef = stepCall.stepDef) {
            is BasicStepDef -> result += stepDef.className
            is ComposedStepDef -> {
                for (childStepCall in stepDef.stepCalls) {
                    addGlueClassNames(result, childStepCall)
                }
            }
        }
    }

}
