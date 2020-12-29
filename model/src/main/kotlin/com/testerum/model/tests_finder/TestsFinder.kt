package com.testerum.model.tests_finder

import com.testerum.common_kotlin.canonicalize
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.walk
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import org.slf4j.LoggerFactory
import java.nio.file.Path as JavaPath

object TestsFinder {

    private val LOG = LoggerFactory.getLogger(TestsFinder::class.java)

    fun loadTestsToRun(testPaths: List<TestPath>,
                       tagsToInclude: List<String>,
                       tagsToExclude: List<String>,
                       testsDirectoryRoot: JavaPath,
                       loadTestAtPath: (path: Path) -> TestModel?): Map<TestPath, TestModel> {
        val result = LinkedHashMap<TestPath, TestModel>()

        // load all tests (except manual tests)
        val allTestPaths = findTestsUnderDirectory(testsDirectoryRoot)
        val allTests = LinkedHashMap<TestPath, TestModel>()

        for (testPath in allTestPaths) {
            val test = loadTest(testPath, testsDirectoryRoot, loadTestAtPath)
                    ?: continue

            if (!test.properties.isManual) {
                allTests[TestTestPath(testPath)] = test
            }
        }

        addTestsFromTagsToInclude(result, allTests, tagsToInclude)

        removeTestsFromTagsToExclude(result, tagsToExclude)

        addTestsThatAreSelected(result, testPaths, allTests, tagsToInclude, tagsToExclude)

        return result
    }

    private fun addTestsThatAreSelected(
        result: LinkedHashMap<TestPath, TestModel>,
        testPaths: List<TestPath>,
        allTests: LinkedHashMap<TestPath, TestModel>,
        tagsToInclude: List<String>,
        tagsToExclude: List<String>
    ) {
        if (testPaths.isEmpty() && tagsToInclude.isEmpty()) {
            result.putAll(allTests)
            removeTestsFromTagsToExclude(result, tagsToExclude)
        } else {
            val canonicalTestFileOrDirPaths = testPaths.map {
                when (it) {
                    is FeatureTestPath -> FeatureTestPath(it.featureDir.canonicalize())
                    is TestTestPath -> TestTestPath(it.testFile.canonicalize())
                    is ScenariosTestPath -> ScenariosTestPath(it.testFile.canonicalize(), it.scenarioIndexes)
                }
            }
            for ((path, test) in allTests) {
                for (canonicalTestFileOrDirPath in canonicalTestFileOrDirPaths) {
                    when (canonicalTestFileOrDirPath) {
                        is TestTestPath, is ScenariosTestPath -> if (path.javaPath.startsWith(canonicalTestFileOrDirPath.javaPath)) {
                            result[canonicalTestFileOrDirPath] = test
                        }
                        is FeatureTestPath -> if (path.javaPath.startsWith(canonicalTestFileOrDirPath.javaPath)) {
                            result[path] = test
                        }
                    }
                }
            }
        }
    }

    private fun removeTestsFromTagsToExclude(
        result: LinkedHashMap<TestPath, TestModel>,
        tagsToExclude: List<String>
    ) {
        result.iterator().also { iterator ->
            for ((_, test) in iterator) {
                if (test.tags.any { it in tagsToExclude }) {
                    iterator.remove()
                }
            }
        }
    }

    private fun addTestsFromTagsToInclude(
        result: LinkedHashMap<TestPath, TestModel>,
        allTests: LinkedHashMap<TestPath, TestModel>,
        tagsToInclude: List<String>
    ) {
        for ((path, test) in allTests) {
            if (test.tags.any { it in tagsToInclude }) {
                result[path] = test
            }
        }
    }

    private fun loadTest(testPath: JavaPath,
                         testsDirectoryRoot: JavaPath,
                         loadTestAtPath: (path: Path) -> TestModel?): TestModel? {
        return try {
            val relativeTestPath = testsDirectoryRoot.relativize(testPath)
            val testerumPath = Path.createInstance(relativeTestPath.toString())

            loadTestAtPath(testerumPath)
                    ?: throw RuntimeException("could not find test at [${testPath.toAbsolutePath().normalize()}]")
        } catch (e: Exception) {
            LOG.warn("failed to load test at [${testPath.toAbsolutePath().normalize()}]", e)

            null
        }
    }

    private fun findTestsUnderDirectory(path: JavaPath): List<JavaPath> {
        val result = mutableListOf<JavaPath>()

        path.walk {
            if (it.isRegularFile && it.hasExtension(".test")) {
                result.add(it.canonicalize())
            }
        }

        result.sortBy { it.toString() }

        return result
    }
}
