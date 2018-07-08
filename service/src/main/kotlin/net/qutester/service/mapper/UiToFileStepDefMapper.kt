package net.qutester.service.mapper

import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
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
import net.qutester.model.arg.Arg
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.StepCall
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.StepPatternPart
import net.qutester.model.text.parts.TextStepPatternPart
import net.qutester.service.mapper.file_arg_transformer.FileArgTransformer
import net.qutester.service.mapper.util.ArgNameCodec
import net.qutester.service.mapper.util.UniqueNamesFileStepVarContainer
import java.lang.Exception

open class UiToFileStepDefMapper {

    companion object {
        private val NEWLINES_REGEX = Regex("[\r\n]")
    }

    fun mapToFileModel(composedStepDef: ComposedStepDef): FileStepDef {
        val fileStepDefSignature: FileStepDefSignature = mapSignature(composedStepDef)
        val fileDescription: String? = composedStepDef.description
        val fileSteps: List<FileStepCall> = mapStepCalls(composedStepDef.stepCalls)

        return FileStepDef(fileStepDefSignature, fileDescription, fileSteps)
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
            is TextStepPatternPart  -> FileTextStepDefSignaturePart(part.text)
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

                    val content: String = FileArgTransformer.jsonToFileFormat(arg.content, arg.type)

                    val path: Path? = arg.path
                    if (path != null) {
                        // this is an external resource
                        // the content is saved separately
                        // here, we only need to reference it
                        result.add(
                                FileArgStepCallPart("file:${path.fileName}.${path.fileExtension}")
                        )
                    } else {
                        val argName: String = if (arg.name?.isNotBlank() == true) {
                            arg.name!!
                        } else {
                            patternPart.name
                        }

                        val introduceVariable: Boolean = argName.isNotBlank() || content.contains(NEWLINES_REGEX) || (content.length > 80)

                        if (introduceVariable) {
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
