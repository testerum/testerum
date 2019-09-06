package com.testerum.file_service.mapper.business_to_file.common

import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.file_service.mapper.util.ArgNameCodec
import com.testerum.file_service.mapper.util.UniqueNamesFileStepVarContainer
import com.testerum.model.arg.Arg
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.step.StepCall
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartSerializer
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart

class BusinessToFileStepCallMapper(private val businessToFilePhaseMapper: BusinessToFilePhaseMapper) {

    companion object {
        private val NEWLINES_REGEX = Regex("[\r\n]")
    }

    fun mapStepCalls(stepCalls: List<StepCall>): List<FileStepCall> = stepCalls.map { mapStepCall(it) }

    fun mapStepCall(stepCall: StepCall): FileStepCall {
        val varsContainer = UniqueNamesFileStepVarContainer()

        return FileStepCall(
                phase = businessToFilePhaseMapper.mapPhase(stepCall.stepDef.phase),
                parts = mapStepCallParts(stepCall, varsContainer),
                vars = varsContainer.getVars(),
                enabled = stepCall.enabled
        )
    }

    private fun mapStepCallParts(stepCall: StepCall,
                                 varsContainer: UniqueNamesFileStepVarContainer): List<FileStepCallPart> {
        val result = mutableListOf<FileStepCallPart>()

        var currentParamArgIndex = 0

        for (patternPart in stepCall.stepDef.stepPattern.patternParts) {
            val stepCallPart: FileStepCallPart = when (patternPart) {
                is TextStepPatternPart -> FileTextStepCallPart(patternPart.text)
                is ParamStepPatternPart -> {
                    val arg: Arg = stepCall.args[currentParamArgIndex]
                    currentParamArgIndex++

                    mapParamStepPart(patternPart, arg, varsContainer)
                }
                else -> throw RuntimeException("unknown step pattern part [${patternPart.javaClass.name}]")
            }

            result.add(stepCallPart)
        }

        return result
    }

    private fun mapParamStepPart(patternPart: ParamStepPatternPart,
                                 arg: Arg,
                                 varsContainer: UniqueNamesFileStepVarContainer): FileStepCallPart {
        val content: String = arg.content?.let {
            FileArgTransformer.jsonToFileFormat(it, arg.typeMeta.fileType())
        }.orEmpty()

        val path: Path? = arg.path
        if (path != null) {
            // this is an external resource
            // the content is saved separately
            // here, we only need to reference it
            return FileArgStepCallPart("file:$path")
        }

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

            return FileArgStepCallPart(varReference)
        } else {
            return FileArgStepCallPart(content)
        }
    }

}
