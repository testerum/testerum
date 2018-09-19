package com.testerum.file_service.caches.resolved

import com.testerum.file_service.caches.resolved.resolvers.TestResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.TestFileService
import com.testerum.file_service.util.isChangedRequiringSave
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class TestsCache(private val testFileService: TestFileService,
                 private val testResolver: TestResolver,
                 private val warningService: WarningService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(TestsCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var testsDir: JavaPath? = null
    private var resourcesDir: JavaPath? = null

    private var testsByPath: MutableMap<Path, TestModel> = hashMapOf()

    fun initialize(testsDir: JavaPath, resourcesDir: JavaPath) {
        lock.write {
            val startTimeMillis = System.currentTimeMillis()

            this.testsDir = testsDir
            this.resourcesDir = resourcesDir

            val newTestsByPath = HashMap<Path, TestModel>()

            val tests = testFileService.getAllTests(testsDir)
            for (test in tests) {
                val resolvedTest = testResolver.resolveStepsDefs(test, resourcesDir)
                val resolvedTestWithWarnings = warningService.testWithWarnings(resolvedTest, keepExistingWarnings = false)

                newTestsByPath[resolvedTestWithWarnings.path] = resolvedTestWithWarnings
            }

            testsByPath = newTestsByPath

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${newTestsByPath.size} tests took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    fun getAllTests(): Collection<TestModel> = lock.read { testsByPath.values }

    fun getTestAtPath(path: Path): TestModel? = lock.read { testsByPath[path] }

    fun getTestsForPaths(testOrDirectoryPaths: List<Path>): List<TestModel> {
        lock.read {
            return testsByPath.values.filter { test ->
                test.path.isChildOrSelfOfAny(testOrDirectoryPaths)
            }
        }
    }

    fun directoryWasRenamed(oldPath: Path, newPath: Path) {
        lock.write {
            val newTestsByPath = HashMap<Path, TestModel>()

            for ((testPath, test) in testsByPath) {
                val newTestPath = testPath.replaceDirs(oldPath, newPath)

                newTestsByPath[newTestPath] = test.copy(
                        path = newTestPath,
                        oldPath = newTestPath
                )
            }

            testsByPath = newTestsByPath
        }
    }

    fun save(test: TestModel): TestModel {
        lock.write {
            val testsDir = this.testsDir
                    ?: throw IllegalStateException("cannot save composed step because the testsDir is not set")
            val resourcesDir = this.resourcesDir
                    ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")

            val existingTest = test.oldPath?.let { testsByPath[it] }

            // update without changes
            if (existingTest != null && !test.isChangedRequiringSave(existingTest)) {
                return existingTest
            }

            // save file
            val savedTest = testFileService.save(test, testsDir)

            // resolve test
            val resolvedTest = testResolver.resolveStepsDefs(savedTest, resourcesDir)
            val resolvedTestWithWarnings = warningService.testWithWarnings(resolvedTest, keepExistingWarnings = false)

            // update cache
            testsByPath.remove(test.path)
            testsByPath[resolvedTestWithWarnings.path] = resolvedTestWithWarnings

            return savedTest
        }
    }

    fun deleteTest(path: Path) {
        lock.write {
            val testsDir = this.testsDir
                    ?: throw IllegalStateException("cannot save composed step because the testsDir is not set")
            val resourcesDir = this.resourcesDir
                    ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")

            testFileService.deleteTest(path, testsDir)

            initialize(testsDir, resourcesDir)
        }
    }

    fun moveTestDirectoryOrFile(copyPath: CopyPath) {
        lock.write {
            val testsDir = this.testsDir
                    ?: throw IllegalStateException("cannot save composed step because the testsDir is not set")
            val resourcesDir = this.resourcesDir
                    ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")

            testFileService.moveTestDirectoryOrFile(copyPath, testsDir)

            initialize(testsDir, resourcesDir)
        }
    }

    fun featureWasDeleted() {
        lock.write {
            val testsDir = this.testsDir
                    ?: throw IllegalStateException("cannot save composed step because the testsDir is not set")
            val resourcesDir = this.resourcesDir
                    ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")

            initialize(testsDir, resourcesDir)
        }
    }

}
