package com.testerum.test_file_format.stepdef.signature

import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart

data class FileStepDefSignature(val phase: FileStepPhase,
                                val parts: List<FileStepDefSignaturePart>)
