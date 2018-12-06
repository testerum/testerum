package com.testerum.runner_cmdline.cmdline.params.model

import com.testerum.runner.cmdline.OutputFormat
import java.nio.file.Path as JavaPath

data class CmdlineParams(val repositoryDirectory: JavaPath,
                         val basicStepsDirectory: JavaPath,
                         val settingsFile: JavaPath?,
                         val settingOverrides: Map<String, String>,
                         val testFilesOrDirectories: List<JavaPath>,
                         val verbose: Boolean,
                         val outputFormatsWithProperties: List<String>) {

    companion object {
        val DEFAULT_OUTPUT_FORMAT = OutputFormat.TREE
    }

}
