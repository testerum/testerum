package com.testerum.file_service.mapper.business_to_file

import com.testerum.file_service.mapper.business_to_file.common.BusinessToFilePhaseMapper
import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.FileStepDef
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart

class BusinessToFileStepMapper(private val businessToFilePhaseMapper: BusinessToFilePhaseMapper,
                               private val businessToFileStepCallMapper: BusinessToFileStepCallMapper) {

    fun mapComposedStep(composedStep: ComposedStepDef): FileStepDef {
        return FileStepDef(
                signature = mapSignature(composedStep),
                description = composedStep.description,
                tags = composedStep.tags,
                steps = businessToFileStepCallMapper.mapStepCalls(composedStep.stepCalls)
        )
    }

    private fun mapSignature(composedStep: ComposedStepDef): FileStepDefSignature {
        val filePhase: FileStepPhase = businessToFilePhaseMapper.mapPhase(composedStep.phase)
        val fileSignatureParts: List<FileStepDefSignaturePart> = mapSignatureParts(composedStep.stepPattern.patternParts)

        return FileStepDefSignature(filePhase, fileSignatureParts)
    }

    private fun mapSignatureParts(parts: List<StepPatternPart>): List<FileStepDefSignaturePart> {
        return parts.map { mapSignaturePart(it) }
    }

    private fun mapSignaturePart(part: StepPatternPart): FileStepDefSignaturePart {
        return when (part) {
            is TextStepPatternPart  -> FileTextStepDefSignaturePart(part.text)
            is ParamStepPatternPart -> FileParamStepDefSignaturePart(part.name, part.type) //TODO: what about the part.description and part.enumValues
            else                    -> throw Exception("unknown StepPatternPart [${part.javaClass.name}]")
        }
    }

}
