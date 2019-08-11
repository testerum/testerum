package com.testerum.runner_cmdline.cmdline.params.model

import com.testerum.model.tests_finder.TestPath
import java.nio.file.Path as JavaPath

data class CmdlineParams(val verbose: Boolean,
                         val repositoryDirectory: JavaPath,
                         val basicStepsDirectory: JavaPath,
                         val variablesEnvironment: String?,
                         val variableOverrides: Map<String, String>,
                         val settingsFile: JavaPath?,
                         val settingOverrides: Map<String, String>,
                         val testPaths: List<TestPath>,
                         val tagsToInclude: List<String>,
                         val tagsToExclude: List<String>,
                         val reportsWithProperties: List<String>,
                         val managedReportsDir: JavaPath?,
                         val executionName: String?)
