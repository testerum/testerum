package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.test_file_format.manual_step_call.status.FileManualStepCallStatus

class BusinessToFileManualStepStatusMapper {

    fun mapStatus(status: ManualTestStepStatus): FileManualStepCallStatus {
        return when (status) {
            ManualTestStepStatus.NOT_EXECUTED -> FileManualStepCallStatus.NOT_EXECUTED
            ManualTestStepStatus.PASSED       -> FileManualStepCallStatus.PASSED
            ManualTestStepStatus.FAILED       -> FileManualStepCallStatus.FAILED
        }
    }

}