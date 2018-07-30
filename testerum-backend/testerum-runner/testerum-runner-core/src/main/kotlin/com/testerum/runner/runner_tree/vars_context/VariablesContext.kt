package com.testerum.runner.runner_tree.vars_context

import com.testerum.api.test_context.test_vars.VariableNotFoundException
import com.testerum.common.expression_evaluator.UnknownVariableException
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart
import net.qutester.model.arg.Arg
import net.qutester.model.step.StepCall
import net.qutester.model.step.StepDef
import net.qutester.model.text.parts.ParamStepPatternPart

class VariablesContext private constructor(private val argsVars: Map<String, Any?>,
                                           private val dynamicVars: DynamicVariablesContext,
                                           private val globalVars: GlobalVariablesContext) {

    companion object {
        // todo: support escape sequences
        private val variableUsageRegex = Regex("""\{\{([^}]+)}}""")

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

    fun resolveIn(arg: Arg): Any? {
        val content = arg.content
                ?: return null

        val resolvedArgParts = mutableListOf<Any?>()

        val parts: List<FileArgPart> = ARG_PART_PARSER.parse(content)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart       -> part.text
                is FileExpressionArgPart -> {
                    try {
                        // todo: use ExpressionEvaluator
                        // ExpressionEvaluator.evaluate(part.text, varsMap).toString()
                        get(part.text.trim())
                    } catch (e: UnknownVariableException) {
                        part.text
                    }
                }
            }

            resolvedArgParts += resolvedArgPartPart
        }

        return if (resolvedArgParts.isEmpty()) {
            ""
        } else if (resolvedArgParts.size == 1) {
            // not doing joinToString() to preserve the type
            resolvedArgParts[0]
        } else {
            resolvedArgParts.joinToString(separator = "")
        }
    }

    fun resolveIn(text: String): String
            = variableUsageRegex.replace(text) { matchResult ->
                val varName = matchResult.groups[1]!!.value
                        .trim()

                return@replace get(varName).toString()
            }

}