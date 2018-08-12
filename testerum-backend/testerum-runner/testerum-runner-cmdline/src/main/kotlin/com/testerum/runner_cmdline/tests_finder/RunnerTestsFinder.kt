package com.testerum.runner_cmdline.tests_finder

import com.testerum.model.repository.enums.FileType
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import java.nio.file.Files
import java.nio.file.Path

class RunnerTestsFinder {

    fun findPathsToTestsToExecute(cmdlineParams: CmdlineParams): List<Path> {
        if (cmdlineParams.testFilesOrDirectories.isEmpty()) {
            return findTestsUnderDirectory(
                    cmdlineParams.repositoryDirectory.resolve(FileType.TEST.relativeRootDirectory.toJavaPath())
            )
        } else {
            val result = mutableListOf<Path>()

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

    private fun findTestsUnderDirectory(path: Path): List<Path> {
        val result = mutableListOf<Path>()

        Files.walk(path).use { pathStream ->
            pathStream.forEach { path ->
                if (Files.isRegularFile(path) && path.fileName.toString().endsWith(".test")) {
                    result.add(path.toAbsolutePath().normalize())
                }
            }
        }

        result.sortBy { it.toAbsolutePath().normalize().toString() }

        return result
    }

}
