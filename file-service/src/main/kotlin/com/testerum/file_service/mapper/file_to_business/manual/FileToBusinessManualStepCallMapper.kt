package com.testerum.file_service.mapper.file_to_business.manual

import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.model.manual.ManualStepCall
import com.testerum.test_file_format.manual_step_call.FileManualStepCall

class FileToBusinessManualStepCallMapper(private val stepCallMapper: FileToBusinessStepCallMapper,
                                         private val stepStatusMapper: FileToBusinessManualStepStatusMapper) {

    fun mapStepCalls(fileManualStepCalls: List<FileManualStepCall>,
                     stepCallIdPrefix: String): List<ManualStepCall> {
        val fileStepCalls = fileManualStepCalls.map { it.step }
        val stepsCalls = stepCallMapper.mapStepCalls(fileStepCalls, stepCallIdPrefix)

        val manualStepCalls = mutableListOf<ManualStepCall>()

        for ((i, fileManualStepCall) in fileManualStepCalls.withIndex()) {
            val status = stepStatusMapper.mapStatus(fileManualStepCall.status)

            manualStepCalls += ManualStepCall(
                    stepCall = stepsCalls[i],
                    status = status,
                    enabled = fileManualStepCall.enabled
            )
        }

        return manualStepCalls
    }

}
