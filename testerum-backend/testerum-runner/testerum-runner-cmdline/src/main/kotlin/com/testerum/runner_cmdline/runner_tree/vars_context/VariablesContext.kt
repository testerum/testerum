package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum.api.test_context.test_vars.VariableNotFoundException
import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.model.arg.Arg
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart

class VariablesContext private constructor(private val argsVars: Map<String, Any?>,
                                           private val dynamicVars: DynamicVariablesContext,
                                           private val globalVars: GlobalVariablesContext) {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())

        fun forTest(dynamicVars: DynamicVariablesContext, globalVars: GlobalVariablesContext)
                = VariablesContext(emptyMap(), dynamicVars, globalVars)
    }

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

    private fun toMap(): Map<String, Any?> {
        // todo: this looks slow...
        val result = LinkedHashMap<String, Any?>()

        result.putAll(globalVars.toMap())
        result.putAll(dynamicVars.toMap())
        result.putAll(argsVars)

        // evaluate expressions in values
        for ((key, value) in result) {
            if (value is String) {
                result[key] = evaluateExpressionSafely(value, result)
            }
        }

        return result
    }

    fun resolveIn(arg: Arg): Any? = resolveInText(arg.content, this.toMap())

    private fun resolveInText(text: String?,
                              context: Map<String, Any?>): Any? {
        if (text == null) {
            return null
        }

        val resolvedArgParts = mutableListOf<Any?>()

        val parts: List<FileArgPart> = ARG_PART_PARSER.parse(text)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart       -> part.text
                is FileExpressionArgPart -> {
                    evaluateExpressionSafely(part.text, context)
                }
            }

            resolvedArgParts += resolvedArgPartPart
        }

        return when {
            resolvedArgParts.isEmpty() -> ""
            resolvedArgParts.size == 1 -> resolvedArgParts[0] // not doing joinToString() to preserve the type
            else                       -> resolvedArgParts.joinToString(separator = "")
        }
    }

    // todo: why do we need this, and can we replace it?
    fun resolveIn(text: String): String = resolveInText(text, this.toMap()).toString()

    private fun evaluateExpressionSafely(expression: String,
                                         context: Map<String, Any?>): Any? {
        return try {
            ExpressionEvaluator.evaluate(expression, context)
        } catch (e: Exception) {
            expression
        }
    }

}