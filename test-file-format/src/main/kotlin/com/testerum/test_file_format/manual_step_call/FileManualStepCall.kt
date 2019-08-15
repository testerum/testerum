package com.testerum.test_file_format.manual_step_call

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus

data class FileManualStepCall(val step: FileStepCall,
                              val status: FileManualStepCallStatus = FileManualStepCallStatus.NOT_EXECUTED,
                              val enabled: Boolean = true)
