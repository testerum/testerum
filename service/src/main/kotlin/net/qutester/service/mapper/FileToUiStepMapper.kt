package net.qutester.service.mapper

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.expression_evaluator.UnknownVariableException
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.FileStepCall
import com.testerum.test_file_format.common.step_call.`var`.FileStepVar
import com.testerum.test_file_format.common.step_call.part.FileArgStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileStepCallPart
import com.testerum.test_file_format.common.step_call.part.FileTextStepCallPart
import com.testerum.test_file_format.common.step_call.part.arg_part.*
import com.testerum.test_file_format.common.step_call.phase.FileStepPhase
import com.testerum.test_file_format.stepdef.FileStepDef
import com.testerum.test_file_format.stepdef.signature.part.FileParamStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileStepDefSignaturePart
import com.testerum.test_file_format.stepdef.signature.part.FileTextStepDefSignaturePart
import net.qutester.model.arg.Arg
import net.qutester.model.enums.StepPhaseEnum
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext
import net.qutester.model.step.ComposedStepDef
import net.qutester.model.step.StepCall
import net.qutester.model.step.StepDef
import net.qutester.model.step.UndefinedStepDef
import net.qutester.model.text.StepPattern
import net.qutester.model.text.parts.ParamStepPatternPart
import net.qutester.model.text.parts.StepPatternPart
import net.qutester.model.text.parts.TextStepPatternPart
import net.qutester.service.mapper.file_arg_transformer.FileArgTransformer
import net.qutester.service.mapper.util.ArgNameCodec
import net.qutester.util.StepHashUtil
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.resource_manager.ResourceManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.Exception

open class FileToUiStepMapper(private val resourceManager: ResourceManager) {

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(FileToUiStepMapper::class.java)

        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())
    }

    fun mapToUiModel(fileStepDef: FileStepDef, stepFile: RepositoryFile): ComposedStepDef {
        val uiStepPhase = mapStepPhase(fileStepDef.signature.phase)
        val uiStepPattern = mapSignatureParts(fileStepDef.signature.parts)

        val stepCallIdPrefix = StepHashUtil.calculateStepHash(uiStepPhase, uiStepPattern)
        
        return ComposedStepDef(
                path = stepFile.knownPath.asPath(),
                phase = uiStepPhase,
                stepPattern = uiStepPattern,
                description = fileStepDef.description,
                stepCalls = mapStepsCalls(fileStepDef.steps, stepCallIdPrefix)
        )
    }

    private fun mapStepPhase(filePhase: FileStepPhase): StepPhaseEnum {
        return when (filePhase) {
            FileStepPhase.GIVEN -> StepPhaseEnum.GIVEN
            FileStepPhase.THEN  -> StepPhaseEnum.THEN
            FileStepPhase.WHEN  -> StepPhaseEnum.WHEN
        }
    }

    private fun mapSignatureParts(fileStepDefParts: List<FileStepDefSignaturePart>): StepPattern {
        return StepPattern(
                fileStepDefParts.map { mapSignaturePart(it) }
        )
    }

    private fun mapSignaturePart(filePart: FileStepDefSignaturePart): StepPatternPart {
        return when (filePart) {
            is FileTextStepDefSignaturePart  -> TextStepPatternPart(filePart.text)
            is FileParamStepDefSignaturePart -> ParamStepPatternPart(filePart.name, filePart.type)
            else                             -> throw Exception("unknown FileStepDefSignaturePart [${filePart.javaClass.name}]")
        }
    }

    fun mapStepsCalls(fileStepCalls: List<FileStepCall>, stepCallIdPrefix: String): List<StepCall> {
        val uiSteps = mutableListOf<StepCall>()

        for ((index, fileStepCall) in fileStepCalls.withIndex()) {
            uiSteps.add(
                    StepCall(
                            "$stepCallIdPrefix-$index",
                            mapStepsCall(fileStepCall),
                            mapStepsCallArgs(fileStepCall)
                    )
            )
        }

        return uiSteps
    }

    private fun mapStepsCall(fileStepCall: FileStepCall): StepDef {
        return UndefinedStepDef(
                mapStepPhase(fileStepCall.phase),
                mapStepCallParts(fileStepCall.parts)
        )
    }

    private fun mapStepCallParts(fileStepCallParts: List<FileStepCallPart>): StepPattern {
        return StepPattern(
                fileStepCallParts.map { mapStepCallPart(it) }
        )
    }

    private fun mapStepCallPart(filePart: FileStepCallPart): StepPatternPart {
        return when (filePart) {
            is FileTextStepCallPart -> TextStepPatternPart(filePart.text)
            is FileArgStepCallPart  -> ParamStepPatternPart("", "TEXT") // TODO: refactor model, at this point we don't know details about the param definition
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

        // 1. resolve variables
        // todo: re-write this code to make it easier to understand
        // todo: what to do with the transformers that load resources? remove them? is this generic enough?
        // todo: resolve variables in the test-file-format maven module (create a parser class)
        val argPartWithResolvedVariables: String = buildString {
            val varsMap: Map<String, String> = vars.associateBy(
                    { it.name },
                    { it.value }
            )

            for (argPart: FileArgPart in argParts) {
                val resolvedArgPartPart: String = when (argPart) {
                    is FileTextArgPart -> argPart.text
                    is FileExpressionArgPart -> {
                        try {
                            ExpressionEvaluator.evaluate(argPart.text, varsMap).toString()
                        } catch (e: UnknownVariableException) {
                            FileArgPartSerializer.serializeToString(argPart)
                        }
                    }
                }

                append(resolvedArgPartPart)
            }
        }

        // 2. load resource if text starts with "file:" (ignore if it's escaped)
        var path: Path? = null
        val argContent: String = when {
            argPartWithResolvedVariables.startsWith("file:") -> {
                val pathAsString: String = argPartWithResolvedVariables.removePrefix("file:")

                // todo: don't return as text - return as unresolved Arg
                val fileType: FileType? = FileType.getByFileName(pathAsString)
                if (fileType == null) {
                    LOGGER.warn("cannot find a file type for [$pathAsString], returning arg as TEXT")

                    argPartWithResolvedVariables
                } else {
                    val knownPath = KnownPath(pathAsString, fileType)

                    path = knownPath.asPath()

                    // todo: don't return as text - return as unresolved Arg
                    val resource: ResourceContext? = resourceManager.getResourceByPath(knownPath)
                    if (resource == null) {
                        LOGGER.warn("cannot load the resource at [$pathAsString] (of type [$fileType]), returning arg as TEXT")

                        argPartWithResolvedVariables
                    } else {
                        FileArgTransformer.fileFormatToJson(resource.body, fileType.resourceJavaType)
                    }
                }
            }
            argPartWithResolvedVariables.startsWith("\\file:") -> argPartWithResolvedVariables.substring(1)
            else -> argPartWithResolvedVariables
        }

        // 3. (not here, but where we resolve the step definitions) call transformer (transform from e.g. YML to JSON)

        return Arg(
                name = computeArgName(argParts),
                content = argContent,
                type = "TEXT", // this can only be known after we resolve the step definitions
                path = path
        )
    }

    private fun computeArgName(argParts: List<FileArgPart>): String {
        if (argParts.size != 1) {
            return ""
        }

        val expressionArgPart: FileExpressionArgPart = argParts[0] as? FileExpressionArgPart
                ?: return ""

        // since the only expressions we support is variables, we assume that the entire text is the variable name
        val variableName: String = expressionArgPart.text.trim()

        return ArgNameCodec.variableToArgName(variableName)
    }

}