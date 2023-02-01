package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus

class BusinessToFileManualTestStatusMapper {

    fun mapStatus(status: ManualTestStatus): FileManualTestStatus {
        return when (status) {
            ManualTestStatus.NOT_EXECUTED   -> FileManualTestStatus.NOT_EXECUTED
            ManualTestStatus.IN_PROGRESS    -> FileManualTestStatus.IN_PROGRESS
            ManualTestStatus.PASSED         -> FileManualTestStatus.PASSED
            ManualTestStatus.FAILED         -> FileManualTestStatus.FAILED
            ManualTestStatus.BLOCKED        -> FileManualTestStatus.BLOCKED
            ManualTestStatus.NOT_APPLICABLE -> FileManualTestStatus.NOT_APPLICABLE
        }
    }

}