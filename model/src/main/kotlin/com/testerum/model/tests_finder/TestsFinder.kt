package com.testerum.model.tests_finder

import com.testerum.common_kotlin.canonicalize
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.walk
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import java.nio.file.Path as JavaPath

object TestsFinder {

    fun loadTestsToRun(testFilesOrDirectories: List<JavaPath>,
                       tagsToInclude: List<String>,
                       tagsToExclude: List<String>,
                       testsDirectoryRoot: JavaPath,
                       loadTestAtPath: (path: Path) -> TestModel?): Map<JavaPath, TestModel> {
        val result = LinkedHashMap<JavaPath, TestModel>()

        // load all tests (except manual tests)
        val allTestPaths = findTestsUnderDirectory(testsDirectoryRoot)
        val allTests = LinkedHashMap<JavaPath, TestModel>()

        for (testPath in allTestPaths) {
            val test = loadTest(testPath, testsDirectoryRoot, loadTestAtPath)

            if (!test.properties.isManual) {
                allTests[testPath] = test
            }
        }

        // 1. add those specified in testFilesOrDirectories
        val canonicalTestFileOrDirPaths = testFilesOrDirectories.map { it.canonicalize() }
        for ((path, test) in allTests) {
            if (path.hasAncestor(canonicalTestFileOrDirPaths)) {
                result[path] = test
            }
        }

        // 2. add those specified in tagsToInclude
        for ((path, test) in allTests) {
            if (test.tags.any { it in tagsToInclude }) {
                result[path] = test
            }
        }

        // 3. remove those specified in tagsToExclude
        result.iterator().also { iterator ->
            for ((_, test) in iterator) {
                if (test.tags.any { it in tagsToExclude }) {
                    iterator.remove()
                }
            }
        }

        return result
    }

    private fun loadTest(testPath: JavaPath,
                         testsDirectoryRoot: JavaPath,
                         loadTestAtPath: (path: Path) -> TestModel?): TestModel {
        try {
            val relativeTestPath = testsDirectoryRoot.relativize(testPath)
            val testerumPath = Path.createInstance(relativeTestPath.toString())

            val test = loadTestAtPath(testerumPath)
                    ?: throw RuntimeException("could not find test at [${testPath.toAbsolutePath().normalize()}]")

            return test
        } catch (e: Exception) {
            throw RuntimeException("failed to load test at [${testPath.toAbsolutePath().normalize()}]", e)
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

    private fun JavaPath.hasAncestor(possibleCanonicalAncestors: List<JavaPath>): Boolean {
        for (possibleCanonicalAncestor in possibleCanonicalAncestors) {
            if (this.startsWith(possibleCanonicalAncestor)) {
                return true
            }
        }

        return false
    }
}