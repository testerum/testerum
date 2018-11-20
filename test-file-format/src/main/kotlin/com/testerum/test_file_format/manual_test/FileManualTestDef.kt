package com.testerum.test_file_format.manual_test

import com.testerum.test_file_format.manual_step_call.FileManualStepCall
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus

data class FileManualTestDef(val name: String,
                             val description: String? = null,
                             val tags: List<String> = emptyList(),
                             val steps: List<FileManualStepCall> = emptyList(),
                             val status: FileManualTestStatus = FileManualTestStatus.NOT_EXECUTED,
                             val comments: String? = null)
