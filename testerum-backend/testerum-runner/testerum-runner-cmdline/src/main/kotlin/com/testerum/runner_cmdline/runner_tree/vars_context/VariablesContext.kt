package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.file_service.caches.resolved.resolvers.file_arg_transformer.FileArgTransformer
import com.testerum.model.arg.Arg
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart
import org.apache.commons.text.StringEscapeUtils

class VariablesContext private constructor(private val argsVars: Map<String, Any?>,
                                           private val dynamicVars: DynamicVariablesContext,
                                           private val globalVars: GlobalVariablesContext) {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())

        fun forTest(dynamicVars: DynamicVariablesContext, globalVars: GlobalVariablesContext)
                = VariablesContext(emptyMap(), dynamicVars, globalVars)
    }

    fun getArgsVars(): Map<String, Any?> = argsVars

    fun getDynamicVars(): Map<String, Any?> = dynamicVars.toMap()

    fun getGlobalVars(): Map<String, Any?> = globalVars.toMap()

    fun forStep(stepCall: StepCall): VariablesContext {
        val argsVars = mutableMapOf<String, Any?>()

        val stepDef: StepDef = stepCall.stepDef

        val params: List<ParamStepPatternPart> = stepDef.stepPattern.getParamStepPattern()
        val args: List<Arg> = stepCall.args

        for (i in 0 until params.size) {
            val param: ParamStepPatternPart = params[i]
            val arg: Arg = args[i]

            val paramName: String = param.name

            argsVars[paramName] = this.resolveIn(arg)
        }

        return VariablesContext(argsVars, dynamicVars, globalVars)
    }

    fun contains(name: String): Boolean
            = argsVars.containsKey(name)
              || dynamicVars.containsKey(name)
              || globalVars.containsKey(name)

    operator fun get(name: String): Any?
            = when {
                argsVars.containsKey(name)    -> argsVars[name]
                dynamicVars.containsKey(name) -> dynamicVars[name]
                globalVars.containsKey(name)  -> globalVars[name]
                else                          -> throw VariableNotFoundException(name)
            }

    fun setDynamicVariable(name: String, value: Any?): Any?
            = dynamicVars.set(name, value)

    fun toMap(): Map<String, Any?> {
        // todo: this looks slow...
        val result = LinkedHashMap<String, Any?>()

        result.putAll(globalVars.toMap())
        result.putAll(dynamicVars.toMap())
        result.putAll(argsVars)

        // evaluate expressions in values
        for ((key, value) in result) {
            if (value is String) {
                result[key] = resolveInText(value, result)
            }
        }

        return result
    }

    fun resolveIn(arg: Arg): Any? {
        val escape: (String) -> String = if (FileArgTransformer.shouldTransform(arg.type)) {
            { StringEscapeUtils.escapeJson(it) }
        } else {
            { it }
        }

        return resolveInText(arg.content, this.toMap(), escape)
    }

    private fun resolveInText(text: String?,
                              context: Map<String, Any?>,
                              escape: (String) -> String = {it}): Any? {
        if (text == null) {
            return null
        }

        val resolvedArgParts = mutableListOf<Any?>()

        val parts: List<FileArgPart> = ARG_PART_PARSER.parse(text)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart       -> part.text
                is FileExpressionArgPart -> {
                    val expressionResult = ExpressionEvaluator.evaluate(part.text, context)
                    if (expressionResult is String) {
                        escape(expressionResult)
                    } else {
                        expressionResult
                    }
                }
            }

            resolvedArgParts += resolvedArgPartPart
        }

        return when {
            resolvedArgParts.isEmpty() -> ""
            resolvedArgParts.size == 1 -> resolvedArgParts[0] // not doing joinToString() to preserve the type
            else -> {
                resolvedArgParts.joinToString(
                        separator = "",
                        transform = {
                            if (it is CharSequence) {
                                it
                            } else {
                                escape(it.toString())
                            }

                        }
                )
            }
        }
    }

    // todo: why do we need this, and can we replace it?
    fun resolveIn(text: String, escape: (String) -> String = {it}): String = resolveInText(text, this.toMap(), escape).toString()

}
