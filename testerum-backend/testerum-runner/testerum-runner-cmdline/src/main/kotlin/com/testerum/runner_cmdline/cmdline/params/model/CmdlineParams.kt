package com.testerum.runner_cmdline.cmdline.params.model

import java.nio.file.Path

data class CmdlineParams(val repositoryDirectory: Path,
                         val basicStepsDirectory: Path,
                         val settingsFile: Path?,
                         val settingOverrides: Map<String, String>,
                         val testFilesOrDirectories: List<Path>,
                         val verbose: Boolean,
                         val outputFormat: OutputFormat) {

    enum class OutputFormat {
        TREE,
        JSON
    }

}
