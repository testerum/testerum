package com.testerum.runner_cmdline.cmdline.params.parser

import com.testerum.common_kotlin.isDirectory
import com.testerum.model.tests_finder.FeatureTestPath
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import com.testerum.model.tests_finder.TestTestPath
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

private val testFileWithScenarioRegex = Regex("""(.*\.test)\[([^]]+)]""")

fun parseStringToTestPath(testFilesOrDirectories: List<String>, repositoryDirectory: JavaPath): List<TestPath> {
    return testFilesOrDirectories.map { pathSpecification ->
        parseTestPath(pathSpecification, repositoryDirectory)
    }
}

fun parseTestPath(
    pathSpecification: String,
    repositoryDirectory: JavaPath,
): TestPath {
    val matchResult = testFileWithScenarioRegex.matchEntire(pathSpecification)

    return if (matchResult == null) {
        val javaPathSpecification = Paths.get(pathSpecification)

        val testPath = if (javaPathSpecification.isAbsolute) {
            javaPathSpecification
        } else {
            repositoryDirectory.resolve("features").resolve(pathSpecification)
        }

        val javaPath = getValidatedRequiredFileOrDirectory(testPath)

        if (javaPath.isDirectory) {
            FeatureTestPath(javaPath)
        } else {
            TestTestPath(javaPath)
        }
    } else {
        val javaPathSpecification = Paths.get(matchResult.groupValues[1])

        val scenarioIndexes = matchResult.groupValues[2].split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map {
                parseScenarioIndex(it)
            }

        val testPath = if (javaPathSpecification.isAbsolute) {
            javaPathSpecification
        } else {
            repositoryDirectory.resolve("features").resolve(javaPathSpecification)
        }

        val javaPath = getValidatedRequiredFileOrDirectory(testPath)

        ScenariosTestPath(javaPath, scenarioIndexes)
    }
}

private fun parseScenarioIndex(text: String): Int {
    try {
        val number = text.toInt()

        if (number < 0) {
            throw IllegalArgumentException("invalid scenario index: should not be negative, but found [$number]")
        }

        return number
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException("invalid scenario index: [$text] is not a number", e)
    }
}

private fun getValidatedRequiredFileOrDirectory(path: JavaPath): JavaPath {
    val normalizedPath = path.toAbsolutePath().normalize()
    if (!Files.exists(normalizedPath)) {
        throw CmdlineParamsParserException("test path [$normalizedPath] does not exist")
    }
    if (!Files.isReadable(normalizedPath)) {
        throw CmdlineParamsParserException("test path [$normalizedPath] is not readable; maybe you don't have enough access rights to read this path?")
    }

    return normalizedPath
}

