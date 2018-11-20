package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus

class FileToBusinessManualStepStatusMapper {

    fun mapStatus(status: FileManualStepCallStatus): ManualTestStepStatus {
        return when (status) {
            FileManualStepCallStatus.NOT_EXECUTED -> ManualTestStepStatus.NOT_EXECUTED
            FileManualStepCallStatus.PASSED       -> ManualTestStepStatus.PASSED
            FileManualStepCallStatus.FAILED       -> ManualTestStepStatus.FAILED
        }
    }

}