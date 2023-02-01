package com.testerum.runner_cmdline.cmdline.params.model

import com.testerum.model.tests_finder.TestPath
import java.nio.file.Path

sealed class CmdlineParams

object HelpRequested : CmdlineParams()

object VersionRequested : CmdlineParams()

data class RunCmdlineParams(
    val verbose: Boolean,
    val repositoryDirectory: Path,
    val testPaths: List<TestPath>,
    val packagesWithAnnotations: List<String>,
    val includeTags: List<String>,
    val excludeTags: List<String>,
    val managedReportsDir: Path?,
    val reportsWithProperties: List<String>,
    val settingOverrides: Map<String, String>,
    val settingsFile: Path?,
    val variableOverrides: Map<String, String>,
    val variablesEnvironment: String?,
    val executionName: String?
) : CmdlineParams()

