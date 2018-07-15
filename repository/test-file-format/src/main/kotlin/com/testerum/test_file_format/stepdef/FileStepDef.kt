package com.testerum.test_file_format.stepdef

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature

data class FileStepDef(val signature: FileStepDefSignature,
                       val description: String? = null,
                       val tags: List<String> = emptyList(),
                       val steps: List<FileStepCall> = emptyList())
