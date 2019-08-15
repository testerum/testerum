package com.testerum.file_service.mapper.business_to_file.manual

import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.model.manual.ManualStepCall
import com.testerum.test_file_format.manual_step_call.FileManualStepCall

class BusinessToFileManualStepCallMapper(private val stepCallMapper: BusinessToFileStepCallMapper,
                                         private val stepStatusMapper: BusinessToFileManualStepStatusMapper) {

    fun mapStepCalls(stepCalls: List<ManualStepCall>): List<FileManualStepCall> = stepCalls.map { mapStepCall(it) }

    fun mapStepCall(stepCall: ManualStepCall): FileManualStepCall {
        return FileManualStepCall(
                step = stepCallMapper.mapStepCall(stepCall.stepCall),
                status = stepStatusMapper.mapStatus(stepCall.status),
                enabled = stepCall.enabled
        )
    }

}
