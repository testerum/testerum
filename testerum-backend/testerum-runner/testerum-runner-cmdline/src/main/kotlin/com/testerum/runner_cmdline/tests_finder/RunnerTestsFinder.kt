package com.testerum.runner_cmdline.tests_finder

import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.walk
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import java.nio.file.Files
import java.nio.file.Path as JavaPath

class RunnerTestsFinder {

    fun findPathsToTestsToExecute(cmdlineParams: CmdlineParams, testsDir: JavaPath): List<JavaPath> {
        if (cmdlineParams.testFilesOrDirectories.isEmpty()) {
            return findTestsUnderDirectory(testsDir)
        } else {
            val result = mutableListOf<JavaPath>()

            for (testFilesOrDirectory in cmdlineParams.testFilesOrDirectories) {
                if (Files.isDirectory(testFilesOrDirectory)) {
                    result.addAll(
                            findTestsUnderDirectory(testFilesOrDirectory)
                    )
                } else {
                    if (Files.isRegularFile(testFilesOrDirectory) && testFilesOrDirectory.fileName.toString().endsWith(".test")) {
                        result.add(testFilesOrDirectory.toAbsolutePath().normalize())
                    }
                }
            }

            return result
        }
    }

    private fun findTestsUnderDirectory(path: JavaPath): List<JavaPath> {
        val result = mutableListOf<JavaPath>()

        path.walk {
            if (it.isRegularFile && it.hasExtension(".test")) {
                result.add(it.toAbsolutePath().normalize())
            }
        }

        result.sortBy { it.toAbsolutePath().normalize().toString() }

        return result
    }

}
