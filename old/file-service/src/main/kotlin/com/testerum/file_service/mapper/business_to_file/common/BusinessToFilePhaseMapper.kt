package com.testerum.file_service.mapper.business_to_file.common

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase

class BusinessToFilePhaseMapper {

    fun mapPhase(phase: StepPhaseEnum): FileStepPhase {
        return when (phase) {
            StepPhaseEnum.GIVEN -> FileStepPhase.GIVEN
            StepPhaseEnum.WHEN  -> FileStepPhase.WHEN
            StepPhaseEnum.THEN  -> FileStepPhase.THEN
        }
    }

}
