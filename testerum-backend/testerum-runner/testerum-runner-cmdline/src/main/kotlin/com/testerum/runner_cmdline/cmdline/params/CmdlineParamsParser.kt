package com.testerum.runner_cmdline.cmdline.params

import com.google.common.base.Splitter
import com.testerum.common_kotlin.isDirectory
import com.testerum.model.tests_finder.FeatureTestPath
import com.testerum.model.tests_finder.ScenariosTestPath
import com.testerum.model.tests_finder.TestPath
import com.testerum.model.tests_finder.TestTestPath
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserVersionHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import picocli.CommandLine
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.Path as JavaPath

object CmdlineParamsParser {

    private val testFileWithScenarioRegex = Regex("""(.*\.test)\[([^]]+)]""")
    private val scenarioIndexesSplitter = Splitter.on(",")
            .trimResults()
            .omitEmptyStrings()

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
            name = "testerum-runner",
            showDefaultValues = true,
            requiredOptionMarker = '*'
    )
    private class MutableCmdlineParams {

        @CommandLine.Option(
                names = ["-h", "--help"],
                usageHelp = true,
                description = [
                    "displays this help message and exits",
                    ""
                ]
        )
        var usageHelpRequested: Boolean = false

        @CommandLine.Option(
                names = ["--version"],
                versionHelp = true,
                description = [
                    "displays program version information and exits",
                    ""
                ]
        )
        var versionHelpRequested: Boolean = false

        @CommandLine.Option(
                names = ["--verbose"],
                description = [
                    "log extra information",
                    ""
                ],
                hidden = true
        )
        var verbose: Boolean = false

        @CommandLine.Option(
                names = ["--repository-directory"],
                required = true,
                description = [
                    "path  to   the  root  of  the   repository,  where  the",
                    "test/feature/resource/etc. files are stored",
                    "",
                    "Example:",
                    "    --repository-directory /path/to/repo/dir",
                    ""
                ]
        )
        var repositoryDirectory: JavaPath? = null

        @CommandLine.Option(
                names = ["--var"],
                description = [
                    "override Testerum variables",
                    "",
                    "This argument takes precedence over variables from",
                    "--var-env, if the same key is found in both.",
                    "",
                    "Example:",
                    "    --var URL=http://localhost:4200",
                    "",
                    "See also:",
                    "    --var-env",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var variableOverrides: Map<String, String> = linkedMapOf()

        @CommandLine.Option(
                names = ["--var-env"],
                required = false,
                description = [
                    "the variables environment to use",
                    "",
                    "Example:",
                    "    --var-env dev",
                    "",
                    "See also:",
                    "    --var",
                    ""
                ]
        )
        var variablesEnvironment: String? = null

        @CommandLine.Option(
                names = ["--setting"],
                description = [
                    "override Testerum settings",
                    "",
                    "This argument takes precedence over --settings-file, if",
                    "the same key is found in both.",
                    "",
                    "Example:",
                    "    --setting testerum.selenium.afterStepDelayMillis=0",
                    "",
                    "See also:",
                    "    --settings-file",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var settingOverrides: Map<String, String> = linkedMapOf()

        @CommandLine.Option(
                names = ["--settings-file"],
                description = [
                    "path to a settings file",
                    "",
                    "The settings  in this file override Testerum  settings.",
                    "The  file  must be  in  the  standard Java  .properties",
                    "format.",
                    "",
                    "Example:",
                    "    --settings-file /path/to/file.properties",
                    "",
                    "See also:",
                    "    --setting",
                    ""
                ]
        )
        var settingsFile: JavaPath? = null

        @CommandLine.Option(
                names = ["--test-path"],
                paramLabel = "testPath",
                description = [
                    "path to test file or directory",
                    "",
                    "If no test path is specified, all tests will be run.",
                    "",
                    "If  the  paths  are  relative, they  will  be  resolved",
                    "relative to the features directory.",
                    "",
                    "For a parametrized test, by default all scenarios will",
                    "be run. If you want to run only some scenarios,",
                    "add them to the end of the test filename, within",
                    "square brackets: my-little.test[3]. The number is the",
                    "index of the scenario, starting with 0.",
                    "If you want to run multiple scenarios, use comma to",
                    "separate the indexes: my-little.test[0,2,5]",
                    "",
                    "Example:",
                    "    --test-path \"/path/to/some feature/\"",
                    "    --test-path \"/path/to/another-feature/File 1.test\"",
                    "    --test-path \"another-feature/File 2.test\"",
                    "    --test-path \"a-feature/Parametrized.test[0,2,5]\"",
                    "",
                    "See also:",
                    "    --include-tag",
                    "    --exclude-tag",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var testFilesOrDirectories: List<String> = arrayListOf()

        @CommandLine.Option(
                names = ["--include-tag"],
                paramLabel = "includeTag",
                description = [
                    "test or feature tag used to select what tests will run",
                    "",
                    "If no tag will be specified, the other options",
                    "will be used to determine what to run (e.g. --test-path)",
                    "",
                    "If a test has one of these tags, the test will be",
                    "executed.",
                    "",
                    "If a feature has one of these tags, all tests inside it",
                    "(recursively) will be executed.",
                    "",
                    "Exclude tags have priority over include tags,",
                    "i.e. if a feature or a test have both an include tag",
                    "and an exclude tag, the feature or test will NOT be",
                    "executed.",
                    "",
                    "Example:",
                    "    --include-tag web",
                    "    --include-tag a_tag --include-tag a_second_tag",
                    "    --include-tag \"tag with spaces\"",
                    "",
                    "See also:",
                    "    --exclude-tag",
                    "    --test-path",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var tagsToInclude: List<String> = arrayListOf()

        @CommandLine.Option(
                names = ["--exclude-tag"],
                paramLabel = "excludeTag",
                description = [
                    "test or feature tag used to select what tests",
                    "will NOT run",
                    "",
                    "If a test has one of these tags, the test will NOT be",
                    "executed.",
                    "",
                    "If a feature has one of these tags, all tests inside it",
                    "(recursively) will NOT be executed.",
                    "",
                    "Exclude tags have priority over include tags,",
                    "i.e. if a feature or a test have both an include tag",
                    "and an exclude tag, the feature or test will NOT be",
                    "executed.",
                    "",
                    "Example:",
                    "    --exclude-tag web",
                    "    --exclude-tag a_tag --exclude-tag a_second_tag",
                    "    --exclude-tag \"tag with spaces\"",
                    "",
                    "See also:",
                    "    --include-tag",
                    "    --test-path",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var tagsToExclude: List<String> = arrayListOf()

        @CommandLine.Option(
                names = ["--report"],
                defaultValue = "CONSOLE",
                description = [
                    "what report(s) to produce",
                    "",
                    "The value  of this argument has  the following generic",
                    "syntax:",
                    "    <reportType>[:prop1=val1,prop2=val2,...]",
                    "",
                    "where:",
                    "* reportType is one of the following:",
                    "    CONSOLE",
                    "    CONSOLE_DEBUG",
                    "    PRETTY",
                    "* the properties  are optional, and they  depend on the",
                    "  reportType.",
                    "  Multiple  properties are  separated by  comma (,).",
                    "  Keys  are  separated  from  values  by  equal (=).",
                    "  If the value is empty, the equal sign is optional.",
                    "  If the key or the value must contain  comma (,) or",
                    "  equal (=), they must be escaped  using  backslash.",
                    "  For example:",
                    "    equation=1+2\\=3,greeting=Hello\\\\, world!",
                    "  will produce 2 properties:",
                    "    \"equation\" with the value \"1+2=3\"",
                    "    \"greeting\" with the value \"Hello, world!\"",
                    "",
                    "If \"--report\" is missing, the default value will be:",
                    "    \${DEFAULT-VALUE}.",
                    "",
                    "CONSOLE report type",
                    "-------------------",
                    "This report  shows the test execution  progress and the",
                    "results to the console.",
                    "",
                    "Logs are only shown for failed tests.",
                    "",
                    "This report type doesn't have any properties.",
                    "",
                    "Example:",
                    "    --report CONSOLE",
                    "",
                    "",
                    "CONSOLE_DEBUG report type",
                    "-------------------------",
                    "This reports  shows as much information  as possible to",
                    "aid in debugging problems.",
                    "",
                    "This report type doesn't have any properties.",
                    "",
                    "Example:",
                    "    --report CONSOLE_DEBUG",
                    "",
                    "",
                    "PRETTY report type",
                    "------------------",
                    "This produces an interactive HTML report.",
                    "",
                    "This   report   has   one  required   property   called",
                    "\"destinationDirectory\", which  must be the path  to the",
                    "directory where the report will be written.",
                    "",
                    "Example:",
                    "    --report PRETTY:destinationDirectory=/some/path",
                    "",
                    "If the destination directory doesn't exist yet, it will",
                    "be created.",
                    "",
                    "",
                    "REMOTE_SERVER report type",
                    "-------------------------",
                    "This sends the reports to the Testerum reports server.",
                    "",
                    "This report has one required property called",
                    "\"reportServerUrl\", which must be the URL to the report",
                    "server.",
                    "",
                    "Example:",
                    "--report REMOTE_SERVER:reportServerUrl=http://report-server-hostname:7788",
                    "",
                    "",
                    "",
                    "Multiple reports can be specified at the same time:",
                    "--report CONSOLE --report PRETTY:destinationDirectory=/p",
                    "",
                    "Do  not  specify multiple  report  types  that use  the",
                    "console, for example:",
                    "    --report CONSOLE --report CONSOLE_DEBUG",
                    "If you do this, since  all reports will be interleaved,",
                    "it will be hard to understand the results.",
                    "",
                    "",
                    "See also:",
                    "    --managed-reports-directory",
                    ""
                ],
                showDefaultValue = CommandLine.Help.Visibility.NEVER
        )
        var reports: List<String> = arrayListOf()

        @CommandLine.Option(
                names = ["--managed-reports-directory"],
                description = [
                    "path to the root directory of managed reports.",
                    "",
                    "Normally,  you  need  to  specify  a  different  report",
                    "directory every  time you invoke the  runner, otherwise",
                    "the new report will overwrite the previous one.",
                    "",
                    "If this argument  is present, the runner  will create a",
                    "separate  directory  at  every  execution.  This  makes",
                    "automation  easier (e.g.  from shell  scripts or  CI/CD",
                    "servers).",
                    "",
                    "In addition, a latest-report.html  file will be created",
                    "that always shows the latest report.",
                    "",
                    "",
                    "See also:",
                    "    --report",
                    ""
                ]
        )
        var managedReportsDir: JavaPath? = null

        @CommandLine.Option(
                names = ["--execution-name"],
                description = [
                    "human-readable title of this execution,",
                    "displayed in the reports",
                    ""
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
                    variablesEnvironment = variablesEnvironment,
                    variableOverrides = variableOverrides,
                    settingsFile = getValidatedSettingsFile(),
                    settingOverrides = settingOverrides,
                    testPaths = getValidatedTestFilesOrDirectories(),
                    tagsToInclude = tagsToInclude,
                    tagsToExclude = tagsToExclude,
                    reportsWithProperties = reports,
                    managedReportsDir = managedReportsDir,
                    executionName = executionName
            )
        }

        private fun getValidatedRepositoryDirectory(): JavaPath = getValidatedRequiredDirectory(repositoryDirectory, "repositoryDirectory")

        private fun getValidatedSettingsFile(): JavaPath? = getValidatedOptionalFile(settingsFile, "settingsFile")

        private fun getValidatedTestFilesOrDirectories(): List<TestPath> {
            return testFilesOrDirectories.map { pathSpecification ->
                val matchResult = testFileWithScenarioRegex.matchEntire(pathSpecification)

                if (matchResult == null) {
                    val javaPathSpecification = Paths.get(pathSpecification)

                    val testPath = if (javaPathSpecification.isAbsolute) {
                        javaPathSpecification
                    } else {
                        repositoryDirectory!!.resolve("features").resolve(pathSpecification)
                    }

                    val javaPath = getValidatedRequiredFileOrDirectory(testPath, "testPath")

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

                    val javaPath = getValidatedRequiredFileOrDirectory(testPath, "testPath")

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

