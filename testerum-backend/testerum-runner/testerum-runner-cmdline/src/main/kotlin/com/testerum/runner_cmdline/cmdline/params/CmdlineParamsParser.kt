package com.testerum.runner_cmdline.cmdline.params

import com.testerum.common_kotlin.readText
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.cmdline.params.model.HelpRequested
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.cmdline.params.model.VersionRequested
import com.testerum.runner_cmdline.cmdline.params.parser.parseTestPath
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

object CmdlineParamsParser {

    fun parse(args: List<String>): CmdlineParams {
        if (args.isEmpty()) {
            return CmdlineParamsBuilder().build()
        }
        if (args.size == 1 && args[0] == "--help" || args[0] == "-h") {
            return HelpRequested
        }
        if (args.size == 1 && args[0] == "--version") {
            return VersionRequested
        }
        if (args.size == 1 && args[0][0] == '@') {
            return parseArgsFile(args[0])
        }

        return parseNormalArgs(args)
    }

    private fun parseArgsFile(arg: String): CmdlineParams {
        val file = getValidatedFile(
            Paths.get(arg.substring(1)),
            "@argsFile"
        )
        val fileContent = file.readText(charset = Charsets.UTF_8)

        val args = ArgsFileParser.parseArgsFile(fileContent)

        return parseNormalArgs(args)
    }

    private fun parseNormalArgs(args: List<String>): RunCmdlineParams {
        val builder = CmdlineParamsBuilder()

        val argsIterator = ArgsIterator(args)

        while (argsIterator.hasNext()) {
            try {
                val arg = argsIterator.next()

                when {
                    arg == "--repository-directory" -> parseRepositoryDirectory(argsIterator, builder)
                    arg == "--package-with-annotations" -> parsePackageWithAnnotations(argsIterator, builder)
                    arg == "--test-path" -> parseTestPath(argsIterator, builder)
                    arg == "--include-tag" -> parseIncludeTag(argsIterator, builder)
                    arg == "--exclude-tag" -> parseExcludeTag(argsIterator, builder)
                    arg == "--managed-reports-directory" -> parseManagedReportsDirectory(argsIterator, builder)
                    arg == "--report" -> parseReport(argsIterator, builder)
                    arg == "--setting" -> parseSetting(argsIterator, builder)
                    arg == "--settings-file" -> parseSettingsFile(argsIterator, builder)
                    arg == "--var" -> parseVar(argsIterator, builder)
                    arg == "--var-env" -> parseVarEnv(argsIterator, builder)
                    arg.startsWith("-") -> throw CmdlineParamsParserException("unexpected argument [$arg]")
                }
            } catch (e: Exception) {
                throw CmdlineParamsParserException("${e.message} at argument number ${argsIterator.currentIndex() + 1}")
            }
        }

        return builder.build()
    }

    private fun parseRepositoryDirectory(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val dir = Paths.get(
            argsIterator.requiredNextArg("--repository-directory")
        )

        builder.repositoryDirectory(
            getValidatedDirectory(dir, "repositoryDirectory")
        )
    }

    private fun parsePackageWithAnnotations(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val packageName = argsIterator.requiredNextArg("--package-with-annotations")

        builder.addPackageWithAnnotations(packageName)
    }

    private fun parseTestPath(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val pathSpecification = argsIterator.requiredNextArg("--test-path")
        val repositoryDirectory = builder.repositoryDirectory()
            ?: throw CmdlineParamsParserException("[--test-path] must be specified after [--repository-directory]")
        val testPath = parseTestPath(pathSpecification, repositoryDirectory)

        builder.addTestPath(testPath)
    }

    private fun parseIncludeTag(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val tag = argsIterator.requiredNextArg("--include-tag")

        builder.addIncludeTag(tag)
    }

    private fun parseExcludeTag(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val tag = argsIterator.requiredNextArg("--exclude-tag")

        builder.addExcludeTag(tag)
    }

    private fun parseManagedReportsDirectory(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val dir = Paths.get(
            argsIterator.requiredNextArg("--managed-reports-directory")
        )

        builder.managedReportsDir(
            getValidatedDirectory(dir, "managedReportsDirectory")
        )
    }

    private fun parseReport(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val report = argsIterator.requiredNextArg("--report")

        builder.addReportWithProperties(report)
    }

    private fun parseSetting(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val setting = argsIterator.requiredNextArg("--setting")

        val (name, value) = parseKeyValuePair(setting)

        builder.overrideSetting(name, value)
    }

    private fun parseSettingsFile(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val file = Paths.get(
            argsIterator.requiredNextArg("--settings-file")
        )

        builder.settingsFile(
            getValidatedFile(file, "settingsFile")
        )
    }

    private fun parseVar(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val variable = argsIterator.requiredNextArg("--var")

        val (name, value) = parseKeyValuePair(variable)

        builder.overrideVariable(name, value)
    }

    private fun parseVarEnv(
        argsIterator: ArgsIterator,
        builder: CmdlineParamsBuilder
    ) {
        val env = argsIterator.requiredNextArg("--var-env")

        builder.variablesEnvironment(env)
    }

    private fun getValidatedDirectory(directory: Path?, directoryLabel: String): Path {
        val nonNullDirectory: Path = directory
            ?: throw CmdlineParamsParserException("missing $directoryLabel")

        val normalizedDirectory = nonNullDirectory.toAbsolutePath().normalize()
        if (!Files.exists(normalizedDirectory)) {
            throw CmdlineParamsParserException("$directoryLabel [$normalizedDirectory] does not exist")
        }
        if (!Files.isDirectory(normalizedDirectory)) {
            throw CmdlineParamsParserException("$directoryLabel [$normalizedDirectory] is not a directory")
        }

        return normalizedDirectory
    }

    private fun getValidatedFile(file: Path, fileLabel: String): Path {
        val nonNullFile: Path = file

        val normalizedFile = nonNullFile.toAbsolutePath().normalize()
        if (!Files.exists(normalizedFile)) {
            throw CmdlineParamsParserException("$fileLabel [$normalizedFile] does not exist")
        }
        if (Files.isDirectory(normalizedFile)) {
            throw CmdlineParamsParserException("$fileLabel [$normalizedFile] is a directory, but a file is expected")
        }
        if (!Files.isReadable(normalizedFile)) {
            throw CmdlineParamsParserException("$fileLabel [$normalizedFile] is not readable; maybe you don't have enough access rights to read this file?")
        }

        return normalizedFile
    }

    private fun parseKeyValuePair(text: String): Pair<String, String> {
        val indexOfEquals = text.indexOf('=')

        if (indexOfEquals == -1) {
            throw IllegalArgumentException("illegal argument value [$text]; it should be a name=value pair (separated by equals)")
        }

        return Pair(
            text.substring(0, indexOfEquals - 1),
            text.substring(indexOfEquals + 1)
        )
    }

    private class ArgsIterator(private val args: List<String>) {
        private var indexOfNextReturn = 0

        fun currentIndex() = indexOfNextReturn - 1

        fun hasNext() = indexOfNextReturn < args.size

        fun next() = args[indexOfNextReturn++]

        fun requiredNextArg(argLabel: String): String {
            if (!hasNext()) {
                throw CmdlineParamsParserException("missing value for [$argLabel]")
            }

            return next()
        }
    }

}
