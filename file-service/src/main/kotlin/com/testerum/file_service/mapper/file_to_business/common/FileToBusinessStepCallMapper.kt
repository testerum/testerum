package com.testerum.file_service.mapper.file_to_business.common

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common_kotlin.emptyToNull
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.mapper.util.ArgNameCodec
import com.testerum.model.arg.Arg
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.step.UndefinedStepDef
import com.testerum.model.text.StepPattern
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.StepPatternPart
import com.testerum.model.text.parts.TextStepPatternPart
import com.testerum.model.text.parts.param_meta.type.StringTypeMeta
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.part.arg_part.*

class FileToBusinessStepCallMapper(private val phaseMapper: FileToBusinessPhaseMapper) {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())
    }

    /**
     * Maps a list of file step calls to a list of business step calls, with the following limitations:
     * - the definition will always be [UndefinedStepDef]. The real definition will be resolved in the [StepsCache]
     * - argument types will always be ``TEXT`` (we can know the type only after we know the definition)
     * - external resources are NOT loaded here - this will also be done when resolving
     */
    fun mapStepCalls(fileStepCalls: List<FileStepCall>, stepCallIdPrefix: String): List<StepCall> {
        val uiSteps = mutableListOf<StepCall>()

        for ((index, fileStepCall) in fileStepCalls.withIndex()) {
            uiSteps.add(
                    StepCall(
                            id = "$stepCallIdPrefix-$index",
                            stepDef = mapStepsCall(fileStepCall),
                            args = mapStepsCallArgs(fileStepCall),
                            warnings = emptyList(),
                            enabled = fileStepCall.enabled
                    )
            )
        }

        return uiSteps
    }

    private fun mapStepsCall(fileStepCall: FileStepCall): StepDef {
        return UndefinedStepDef( // at this point, we don't know the definition; we will only know it after step resolving
                phase = phaseMapper.mapStepPhase(fileStepCall.phase),
                stepPattern = mapStepCallParts(fileStepCall.parts, fileStepCall.vars)
        )
    }

    private fun mapStepCallParts(fileStepCallParts: List<FileStepCallPart>,
                                 vars: List<FileStepVar>): StepPattern {
        val patternParts = mutableListOf<StepPatternPart>()

        var patternPartIndex = -1
        for (filePart in fileStepCallParts) {
            if (filePart is FileArgStepCallPart) {
                patternPartIndex++
            }

            val varName = if (patternPartIndex >= 0 && patternPartIndex < vars.size) {
                vars[patternPartIndex].name
            } else {
                ""
            }

            patternParts += mapStepCallPart(filePart, varName)
        }

        return StepPattern(patternParts)
    }

    private fun mapStepCallPart(filePart: FileStepCallPart,
                                varName: String): StepPatternPart {
        return when (filePart) {
            is FileTextStepCallPart -> TextStepPatternPart(filePart.text)
            is FileArgStepCallPart  -> ParamStepPatternPart(varName, StringTypeMeta()) // at this point we don't know anything about how this parameter is defined (actual name or type); this will come later, after step resolving
            else                    -> throw Exception("unknown FileStepCallPart [${filePart.javaClass.name}]")
        }
    }

    private fun mapStepsCallArgs(fileStepCall: FileStepCall): List<Arg> {
        return fileStepCall.parts
                .asSequence()
                .filterIsInstance<FileArgStepCallPart>()
                .map { mapStepCallArg(it, fileStepCall.vars) }
                .toList()
    }

    private fun mapStepCallArg(argStepCallPart: FileArgStepCallPart, vars: List<FileStepVar>): Arg {
        val argParts: List<FileArgPart> = ARG_PART_PARSER.parse(argStepCallPart.text)

        val argPartWithResolvedVariables: String = resolveVariables(argParts, vars)

        return Arg(
                name    = computeArgName(argParts, vars),
                content = argPartWithResolvedVariables.emptyToNull(),
                type    = "TEXT", // this can only be known after we resolve the step definitions
                path    = null    // when resolving, this will be replaced with the real path if this argument is an external resource
        )
    }

    private fun resolveVariables(argParts: List<FileArgPart>, vars: List<FileStepVar>) = buildString {
        val varsMap: Map<String, String> = vars.associateBy(
                { it.name },
                { it.value }
        )

        for (argPart: FileArgPart in argParts) {
            val resolvedArgPartPart: String = when (argPart) {
                is FileTextArgPart       -> argPart.text
                is FileExpressionArgPart -> varsMap[argPart.text] ?: FileArgPartSerializer.serializeToString(argPart)
            }

            append(resolvedArgPartPart)
        }
    }

    private fun computeArgName(argParts: List<FileArgPart>,
                               vars: List<FileStepVar>): String? {
        if (argParts.size != 1) {
            return ""
        }

        val expressionArgPart: FileExpressionArgPart = argParts[0] as? FileExpressionArgPart
                ?: return ""

        // if we have any variable with this name, we will use it as the arg name
        val variableName: String = expressionArgPart.text.trim()

        val variableExists = vars.any { it.name == variableName }

        return if (variableExists) {
            ArgNameCodec.variableToArgName(variableName)
        } else {
            null
        }
    }

}
