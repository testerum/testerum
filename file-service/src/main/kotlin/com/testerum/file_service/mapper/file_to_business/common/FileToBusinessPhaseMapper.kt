package com.testerum.file_service.mapper.file_to_business.common

import com.testerum.model.enums.StepPhaseEnum
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase

class FileToBusinessPhaseMapper {

    fun mapStepPhase(filePhase: FileStepPhase): StepPhaseEnum {
        return when (filePhase) {
            FileStepPhase.GIVEN -> StepPhaseEnum.GIVEN
            FileStepPhase.WHEN  -> StepPhaseEnum.WHEN
            FileStepPhase.THEN  -> StepPhaseEnum.THEN
        }
    }

}
