package com.testerum.runner_cmdline.cmdline.params.parser

import com.google.common.base.Splitter
import com.testerum.common_kotlin.isDirectory
import com.testerum.model.tests_finder.FeatureTestPath
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import com.testerum.model.tests_finder.TestTestPath
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

private val testFileWithScenarioRegex = Regex("""(.*\.test)\[([^]]+)]""")
private val scenarioIndexesSplitter = Splitter.on(",")
        .trimResults()
        .omitEmptyStrings()

fun parseStringToTestPath(testFilesOrDirectories: List<String>, repositoryDirectory: Path?, usageHelp: String): List<TestPath> {
    return testFilesOrDirectories.map { pathSpecification ->
        val matchResult = testFileWithScenarioRegex.matchEntire(pathSpecification)

        if (matchResult == null) {
            val javaPathSpecification = Paths.get(pathSpecification)

            val testPath = if (javaPathSpecification.isAbsolute) {
                javaPathSpecification
            } else {
                repositoryDirectory!!.resolve("features").resolve(pathSpecification)
            }

            val javaPath = getValidatedRequiredFileOrDirectory(testPath, "testPath", usageHelp)

            if (javaPath.isDirectory) {
                FeatureTestPath(javaPath)
            } else {
                TestTestPath(javaPath)
            }
        } else {
            val javaPathSpecification = Paths.get(matchResult.groupValues[1])
            val scenarioIndexes = scenarioIndexesSplitter.split(matchResult.groupValues[2])
                    .toList()
                    .map {
                        parseScenarioIndex(it)
                    }

            val testPath = if (javaPathSpecification.isAbsolute) {
                javaPathSpecification
            } else {
                repositoryDirectory!!.resolve("features").resolve(javaPathSpecification)
            }

            val javaPath = getValidatedRequiredFileOrDirectory(testPath, "testPath", usageHelp)

            ScenariosTestPath(javaPath, scenarioIndexes)
        }
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

private fun getValidatedRequiredFileOrDirectory(path: Path, pathLabel: String, usageHelp: String): Path {
    val normalizedPath = path.toAbsolutePath().normalize()
    if (!Files.exists(normalizedPath)) {
        throw CmdlineParamsParserParsingException(
                errorMessage = "$pathLabel [$normalizedPath] does not exist",
                usageHelp = usageHelp
        )
    }
    if (!Files.isReadable(normalizedPath)) {
        throw CmdlineParamsParserParsingException(
                errorMessage = "$pathLabel [$normalizedPath] is not readable; maybe you don't have enough access rights to read this path?",
                usageHelp = usageHelp
        )
    }

    return normalizedPath
}

