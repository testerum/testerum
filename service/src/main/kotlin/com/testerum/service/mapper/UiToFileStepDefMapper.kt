package com.testerum.service.mapper

import com.testerum.model.arg.Arg
import com.testerum.model.enums.StepPhaseEnum
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.StepCall
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.service.mapper.file_arg_transformer.FileArgTransformer
import com.testerum.service.mapper.util.ArgNameCodec
import com.testerum.service.mapper.util.UniqueNamesFileStepVarContainer
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartSerializer
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.FileStepDef
import com.testerum.test_file_format.stepdef.signature.FileStepDefSignature
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import java.lang.Exception

open class UiToFileStepDefMapper {

    companion object {
        private val NEWLINES_REGEX = Regex("[\r\n]")
    }

    fun mapToFileModel(composedStepDef: ComposedStepDef): FileStepDef {

        return FileStepDef(
                signature = mapSignature(composedStepDef),
                description = composedStepDef.description,
                tags = composedStepDef.tags,
                steps = mapStepCalls(composedStepDef.stepCalls)
        )
    }

    private fun mapSignature(composedStepDef: ComposedStepDef): FileStepDefSignature {
        val filePhase: FileStepPhase = mapPhase(composedStepDef.phase)
        val fileSignatureParts: List<FileStepDefSignaturePart> = mapSignatureParts(composedStepDef.stepPattern.patternParts)

        return FileStepDefSignature(filePhase, fileSignatureParts)
    }

    private fun mapPhase(phase: StepPhaseEnum): FileStepPhase {
        return when (phase) {
            StepPhaseEnum.GIVEN -> FileStepPhase.GIVEN
            StepPhaseEnum.WHEN  -> FileStepPhase.WHEN
            StepPhaseEnum.THEN  -> FileStepPhase.THEN
        }
    }

    private fun mapSignatureParts(parts: List<StepPatternPart>): List<FileStepDefSignaturePart> {
        return parts.map { mapSignaturePart(it) }
    }

    private fun mapSignaturePart(part: StepPatternPart): FileStepDefSignaturePart {
        return when (part) {
            is TextStepPatternPart -> FileTextStepDefSignaturePart(part.text)
            is ParamStepPatternPart -> FileParamStepDefSignaturePart(part.name, part.type) //TODO: what about the part.regex and part.enumValues
            else                    -> throw Exception("unknown StepPatternPart [${part.javaClass.name}]")
        }
    }

    fun mapStepCalls(stepCalls: List<StepCall>): List<FileStepCall> = stepCalls.map { mapStepCall(it) }

    private fun mapStepCall(stepCall: StepCall): FileStepCall {
        val varsContainer = UniqueNamesFileStepVarContainer()

        return FileStepCall(
                phase = mapPhase(stepCall.stepDef.phase),
                parts = mapStepCallParts(stepCall, varsContainer),
                vars = varsContainer.getVars()
        )
    }

    private fun mapStepCallParts(stepCall: StepCall,
                                 varsContainer: UniqueNamesFileStepVarContainer): List<FileStepCallPart> {
        val result = mutableListOf<FileStepCallPart>()

        var currentParamArgIndex = 0
        for (patternPart in stepCall.stepDef.stepPattern.patternParts) {
            when (patternPart) {
                is TextStepPatternPart -> {
                    result.add(
                            FileTextStepCallPart(patternPart.text)
                    )
                }
                is ParamStepPatternPart -> {
                    val arg: Arg = stepCall.args[currentParamArgIndex]
                    currentParamArgIndex++

                    val content: String = FileArgTransformer.jsonToFileFormat(arg.content.orEmpty(), arg.type)

                    val path: Path? = arg.path
                    if (path != null) {
                        // this is an external resource
                        // the content is saved separately
                        // here, we only need to reference it
                        result.add(
                                FileArgStepCallPart("file:${path.fileName}.${path.fileExtension}")
                        )
                    } else {
                        val introduceVariable: Boolean = (arg.name?.isNotBlank() == true) || content.contains(NEWLINES_REGEX) || (content.length > 80)

                        if (introduceVariable) {
                            val argName: String = if (arg.name?.isNotBlank() == true) {
                                arg.name!!
                            } else {
                                patternPart.name
                            }

                            val varName: String = ArgNameCodec.argToVariableName(argName)

                            val newVarName = varsContainer.addAndReturnNewName(varName, content)

                            val varReferenceExpression = FileExpressionArgPart(newVarName)
                            val varReference: String = FileArgPartSerializer.serializeToString(varReferenceExpression)

                            result.add(
                                    FileArgStepCallPart(varReference)
                            )

                        } else {
                            result.add(
                                    FileArgStepCallPart(content)
                            )
                        }
                    }
                }
                else -> throw RuntimeException("unknown step pattern part [${patternPart.javaClass.name}]")
            }
        }

        return result
    }

}