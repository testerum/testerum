package com.testerum.test_file_format.testdef

import com.testerum.test_file_format.common.step_call.FileStepCall

data class FileTestDef(val name: String,
                       val description: String? = null,
                       val steps: List<FileStepCall> = emptyList())