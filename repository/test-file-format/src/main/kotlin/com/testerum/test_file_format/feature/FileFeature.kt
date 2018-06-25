package com.testerum.test_file_format.feature

import com.testerum.test_file_format.common.step_call.FileStepCall

data class FileFeature(val description: String? = null,
                       val tags: List<String> = emptyList(),
                       val beforeHooks: List<FileStepCall> = emptyList(),
                       val afterHooks: List<FileStepCall> = emptyList())
