package com.testerum.file_service.mapper.file_to_business

import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessPhaseMapper
import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.util.StepHashUtil
import com.testerum.test_file_format.stepdef.FileStepDef
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import java.nio.file.Path as JavaPath

class FileToBusinessStepMapper(private val phaseMapper: FileToBusinessPhaseMapper,
                               private val callsMapper: FileToBusinessStepCallMapper) {

    fun mapStepDef(fileStepDef: FileStepDef, relativeFilePath: JavaPath): ComposedStepDef {
        val path = Path.createInstance(relativeFilePath.toString())
        val phase = phaseMapper.mapStepPhase(fileStepDef.signature.phase)
        val stepPattern = mapSignatureParts(fileStepDef.signature.parts)

        val stepCallIdPrefix = StepHashUtil.calculateStepHash(phase, stepPattern)
        val stepCalls = callsMapper.mapStepsCalls(fileStepDef.steps, stepCallIdPrefix)

        return ComposedStepDef(
                path = path,
                phase = phase,
                stepPattern = stepPattern,
                description = fileStepDef.description,
                tags = fileStepDef.tags,
                stepCalls = stepCalls
        )
    }

    private fun mapSignatureParts(fileSignatureParts: List<FileStepDefSignaturePart>): StepPattern {
        return StepPattern(
                patternParts = fileSignatureParts.map { mapSignaturePart(it) }
        )
    }

    private fun mapSignaturePart(fileSignaturePart: FileStepDefSignaturePart): StepPatternPart {
        return when (fileSignaturePart) {
            is FileTextStepDefSignaturePart  -> TextStepPatternPart(fileSignaturePart.text)
            is FileParamStepDefSignaturePart -> ParamStepPatternPart(fileSignaturePart.name, fileSignaturePart.type)
            else                             -> throw Exception("unknown FileStepDefSignaturePart [${fileSignaturePart.javaClass.name}]")
        }
    }

}
