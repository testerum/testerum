package com.testerum.test_file_format.feature

import com.testerum.test_file_format.common.step_call.FileStepCall

data class FileFeature(val description: String? = null,
                       val tags: List<String> = emptyList(),
                       val beforeAllHooks: List<FileStepCall> = emptyList(),
                       val beforeEachHooks: List<FileStepCall> = emptyList(),
                       val afterEachHooks: List<FileStepCall> = emptyList(),
                       val afterAllHooks: List<FileStepCall> = emptyList())
