package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.model.arg.Arg
import com.testerum.model.step.StepCall
import com.testerum.model.step.StepDef
import com.testerum.model.text.parts.ParamStepPatternPart
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart
import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import org.apache.commons.text.StringEscapeUtils

class VariablesContext(globalVarsMap: Map<String, String>) {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())

        fun forTest(dynamicVars: DynamicVarsContext, globalVars: GlobalVarsContext) = VariablesContext(emptyMap(), dynamicVars, globalVars)
    }

    private var _argsVars: ArgsContext = null
    private var _dynamicVars: DynamicVarsContext? = null
    private val globalVars = GlobalVarsContext.from(globalVarsMap)

    private val argsVars: ArgsContext
        get() = _argsVars ?: throw RuntimeException("no args context a")

    fun containsKey(name: String): Boolean {
        return argsVars.containsKey(name)
            || dynamicVars.containsKey(name)
            || globalVars.containsKey(name)
    }

    fun get(name: String): Any? {
        return when {
            argsVars.containsKey(name) -> argsVars.get(name)
            dynamicVars.containsKey(name) -> dynamicVars.get(name)
            globalVars.containsKey(name) -> globalVars.get(name)
            else -> throw VariableNotFoundException(name)
        }
    }

    fun setArg(name: String, value: Any?) {
        argsVars.set(name, value)
    }

    fun set(name: String, value: Any?) {
        argsVars.set(name, value)

        // this is needed so that, when we set a dynamic variable with the same name as an argument,
        // this dynamic variable overrides an ancestor arg
        var current: ArgsContext? = argsVars.parent
        while (current != null) {
            if (current.containsKey(name)) {
                current.set(name, value)
            }

            current = current.parent
        }

        dynamicVars.set(name, value)
    }

    fun startSuite() {
    }

    fun endSuite() {
        // should pop one level of dynamicVars, but we don't care what happens after end suite
    }

    fun

    // todo: should we make these 3 toMap() methods public API? in any case, it should be an unmodifiable view

    fun getArgsVars(): Map<String, Any?> = argsVars.toMap()

    fun getDynamicVars(): Map<String, Any?> = dynamicVars.toMap()

    fun getGlobalVars(): Map<String, Any?> = globalVars.toMap()


    fun resolveIn(arg: Arg): Any? {
        val escape: (String) -> String = if (arg.typeMeta is ObjectTypeMeta) {
            { StringEscapeUtils.escapeJson(it) }
        } else {
            { it }
        }

        return resolveInText(arg.content, this.toMap(), escape)
    }

    private fun resolveInText(
        text: String?,
        context: Map<String, Any?>,
        escape: (String) -> String = { it }
    ): Any? {
        if (text == null) {
            return null
        }

        val resolvedArgParts = mutableListOf<Any?>()

        val parts: List<FileArgPart> = ARG_PART_PARSER.parse(text)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart -> part.text
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
    fun resolveIn(text: String, escape: (String) -> String = { it }): String = resolveInText(text, this.toMap(), escape).toString()

}
