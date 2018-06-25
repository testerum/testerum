package com.testerum.runner.cmdline.params.model

import java.nio.file.Path

data class CmdlineParams(val repositoryDirectory: Path,
                         val basicStepsDirectory: Path,
                         val settingsFile: Path?,
                         val settingOverrides: Map<String, String>,
                         val testFilesOrDirectories: List<Path>,
                         val verbose: Boolean,
                         val outputFormat: String) {

    companion object {
        const val DEFAULT_OUTPUT_FORMAT = OutputFormat.TREE
    }

    object OutputFormat {

        // Note:
        //    This object should contain only constants for output formats.
        //    This is because we use reflection to see what are the valid values.

        const val TREE = "TREE"
        const val JSON = "JSON"
    }

}
