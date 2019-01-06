package com.testerum.runner_cmdline.cmdline.params.model

import java.nio.file.Path as JavaPath

data class CmdlineParams(val verbose: Boolean,
                         val repositoryDirectory: JavaPath,
                         val basicStepsDirectory: JavaPath,
                         val settingsFile: JavaPath?,
                         val settingOverrides: Map<String, String>,
                         val testFilesOrDirectories: List<JavaPath>,
                         val reportsWithProperties: List<String>,
                         val managedReportsDir: JavaPath?,
                         val executionName: String?)
