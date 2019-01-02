package com.testerum.runner_cmdline.cmdline.params

import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserVersionHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path as JavaPath

object CmdlineParamsParser {

    fun parse(vararg args: String): CmdlineParams {
        val mutableParams = MutableCmdlineParams()

        try {
            CommandLine.populateCommand(mutableParams, *args)
        } catch (e: CommandLine.PicocliException) {
            throw CmdlineParamsParserParsingException(
                    errorMessage = e.message ?: "",
                    usageHelp = usageToString(mutableParams)
            )
        }

        return mutableParams.getValidatedParams()
    }

    private fun usageToString(command: Any): String {
        val result = ByteArrayOutputStream()

        PrintStream(result, true, StandardCharsets.UTF_8.name()).use { printStream: PrintStream ->
            CommandLine.usage(command, printStream)
        }

        return result.toString(StandardCharsets.UTF_8.name())
    }

    @CommandLine.Command(
            name="testerum-runner",
            showDefaultValues = true,
            requiredOptionMarker = '*'
    )
    private class MutableCmdlineParams {

        @CommandLine.Option(
                names = ["-h", "--help"],
                usageHelp = true,
                description = ["displays this help message and exit"]
        )
        var usageHelpRequested: Boolean = false

        @CommandLine.Option(
                names = ["-V", "--version"],
                versionHelp = true,
                description = ["displays program version information"]
        )
        var versionHelpRequested: Boolean = false

        @CommandLine.Option(
                names = ["-v", "--verbose"],
                description = ["be verbose"]
        )
        var verbose: Boolean = false

        @CommandLine.Option(
                names = ["-r", "--repository-directory"],
                required = true,
                description = ["path to the root of the repository"]
        )
        var repositoryDirectory: JavaPath? = null

        // todo: step directory separation: we should have 2 directories: "built-in" vs "user" basic steps (last one is optional)
        @CommandLine.Option(
                names = ["-b", "--basic-steps-directory"],
                required = true,
                description = ["path to the directory containing the basic steps jars"]
        )
        var basicStepsDirectory: JavaPath? = null

        @CommandLine.Option(
                names = ["-s", "--setting"],
                description = [
                    "settings values overrides",
                    "overrides default settings",
                    "overrides the settings from the settings file, if one is specified"
                ]
        )
        var settingOverrides: Map<String, String> = linkedMapOf()

        @CommandLine.Option(
                names = ["--settings-file"],
                description = [
                    "path to the settings file",
                    "settings in this file override default settings"
                ]
        )
        var settingsFile: JavaPath? = null

        @CommandLine.Option(
                names = ["-t", "--test-path"],
                paramLabel = "testPath",
                description = [
                    "path to test file or directory",
                    "if no test path is specified, all tests will be run"
                ]
        )
        var testFilesOrDirectories: List<JavaPath> = arrayListOf()

        @CommandLine.Option(
                names = ["-o", "--output-format"]
                // todo: description
        )
        var outputFormats: List<String> = arrayListOf()

        @CommandLine.Option(
                names = ["--managed-reports-directory"]
                // todo: description
        )
        var managedReportsDir: JavaPath? = null

        @CommandLine.Option(
                names = ["--execution-name"],
                description = [
                    "human-readable title of this execution",
                    "useful to distinguish one report from another"
                ]
        )
        var executionName: String? = null

        fun getValidatedParams(): CmdlineParams {
            if (usageHelpRequested) {
                throw CmdlineParamsParserHelpRequestedException(
                        usageHelp = usageToString(this)
                )
            }

            if (versionHelpRequested) {
                throw CmdlineParamsParserVersionHelpRequestedException()
            }

            return CmdlineParams(
                    verbose = verbose,
                    repositoryDirectory = getValidatedRepositoryDirectory(),
                    basicStepsDirectory = getValidatedBasicStepsDirectory(),
                    settingsFile = getValidatedSettingsFile(),
                    settingOverrides = settingOverrides,
                    testFilesOrDirectories = getValidatedTestFilesOrDirectories(),
                    outputFormatsWithProperties = if (outputFormats.isNotEmpty()) {
                        outputFormats
                    } else {
                        listOf(CmdlineParams.DEFAULT_OUTPUT_FORMAT.name)
                    },
                    managedReportsDir = managedReportsDir,
                    executionName = executionName
            )
        }

        private fun getValidatedRepositoryDirectory(): JavaPath
                = getValidatedRequiredDirectory(repositoryDirectory, "repositoryDirectory")

        private fun getValidatedBasicStepsDirectory(): JavaPath
                = getValidatedRequiredDirectory(basicStepsDirectory, "basicStepsDirectory")

        private fun getValidatedSettingsFile(): JavaPath?
                = getValidatedOptionalFile(settingsFile, "settingsFile")

        private fun getValidatedTestFilesOrDirectories(): List<JavaPath>
                = testFilesOrDirectories.map { getValidatedRequiredFileOrDirectory(it, "testPath") }

        private fun getValidatedRequiredDirectory(directory: JavaPath?, directoryLabel: String): JavaPath {
            val usageHelp = usageToString(this)

            val nonNullDirectory: JavaPath = directory
                    ?: throw CmdlineParamsParserParsingException(
                            errorMessage = "missing $directoryLabel",
                            usageHelp = usageHelp
                    )

            val normalizedDirectory = nonNullDirectory.toAbsolutePath().normalize()
            if (!Files.exists(normalizedDirectory)) {
                throw CmdlineParamsParserParsingException(
                        errorMessage = "$directoryLabel [$normalizedDirectory] does not exist",
                        usageHelp = usageHelp
                )
            }
            if (!Files.isDirectory(normalizedDirectory)) {
                throw CmdlineParamsParserParsingException(
                        errorMessage = "$directoryLabel [$normalizedDirectory] is not a directory",
                        usageHelp = usageHelp
                )
            }

            return normalizedDirectory
        }

        private fun getValidatedOptionalFile(file: JavaPath?, fileLabel: String): JavaPath? {
            val usageHelp = usageToString(this)

            val nonNullFile: JavaPath = file
                    ?: return null

            val normalizedFile = nonNullFile.toAbsolutePath().normalize()
            if (!Files.exists(normalizedFile)) {
                throw CmdlineParamsParserParsingException(
                        errorMessage = "$fileLabel [$normalizedFile] does not exist",
                        usageHelp = usageHelp
                )
            }
            if (Files.isDirectory(normalizedFile)) {
                throw CmdlineParamsParserParsingException(
                        errorMessage = "$fileLabel [$normalizedFile] is a directory, but a file is expected",
                        usageHelp = usageHelp
                )
            }
            if (!Files.isReadable(normalizedFile)) {
                throw CmdlineParamsParserParsingException(
                        errorMessage = "$fileLabel [$normalizedFile] is not readable; maybe you don't have enough access rights to read this file?",
                        usageHelp = usageHelp
                )
            }

            return normalizedFile
        }

        private fun getValidatedRequiredFileOrDirectory(path: JavaPath, pathLabel: String): JavaPath {
            val usageHelp = usageToString(this)

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
    }

}

