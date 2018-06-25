package com.testerum.test_file_format.common.step_call

import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase

data class FileStepCall(val phase: FileStepPhase,
                        val parts: List<FileStepCallPart>,
                        val vars: List<FileStepVar> = emptyList())
