package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.test_file_format.manual_test.status.FileManualTestStatus

class FileToBusinessManualTestStatusMapper {

    fun mapStatus(status: FileManualTestStatus): ManualTestStatus {
        return when (status) {
            FileManualTestStatus.NOT_EXECUTED   -> ManualTestStatus.NOT_EXECUTED
            FileManualTestStatus.IN_PROGRESS    -> ManualTestStatus.IN_PROGRESS
            FileManualTestStatus.PASSED         -> ManualTestStatus.PASSED
            FileManualTestStatus.FAILED         -> ManualTestStatus.FAILED
            FileManualTestStatus.BLOCKED        -> ManualTestStatus.BLOCKED
            FileManualTestStatus.NOT_APPLICABLE -> ManualTestStatus.NOT_APPLICABLE
        }
    }

}