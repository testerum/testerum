package com.testerum.test_file_format.testdef

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.testdef.properties.FileTestDefProperties
import com.testerum.test_file_format.testdef.scenarios.FileScenario

data class FileTestDef(val name: String,
                       val properties: FileTestDefProperties = FileTestDefProperties.DEFAULT,
                       val description: String? = null,
                       val tags: List<String> = emptyList(),
                       val scenarios: List<FileScenario> = emptyList(),
                       val steps: List<FileStepCall> = emptyList(),
                       val afterHooks: List<FileStepCall> = emptyList())
