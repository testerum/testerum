package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.expression_evaluator.bindings.vars_container.VarsContainer
import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.model.arg.Arg
import com.testerum.model.text.parts.param_meta.ObjectTypeMeta
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileArgPartParserFactory
import com.testerum.test_file_format.common.step_call.part.arg_part.FileExpressionArgPart
import com.testerum.test_file_format.common.step_call.part.arg_part.FileTextArgPart
import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import java.util.TreeMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set
import org.apache.commons.text.StringEscapeUtils

class VariablesContext(globalVarsMap: Map<String, String>) {

    companion object {
        private val ARG_PART_PARSER: ParserExecuter<List<FileArgPart>> = ParserExecuter(FileArgPartParserFactory.argParts())
    }

    private var argsVars = ArgsContext()
    private var dynamicVars = DynamicVarsContext()
    private val globalVars = GlobalVarsContext.from(globalVarsMap)

    val varsContainer = object : VarsContainer {
        override fun containsKey(name: String): Boolean {
            return this@VariablesContext.containsKey(name)
        }

        override fun get(name: String): Any? {
            return this@VariablesContext.get(name)
        }

        override fun set(name: String, value: Any?): Any? {
            val previousValue = if (containsKey(name)) {
                get(name)
            } else {
                null
            }

            this@VariablesContext.set(name, value)

            return previousValue
        }
    }

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
        argsVars.overwriteArgAtAllLevels(name, value)
        dynamicVars.set(name, value)
    }

    fun startSuite() {
        dynamicVars.push()
    }

    fun endSuite() {
        dynamicVars.pop()
    }

    fun startFeature() {
        dynamicVars.push()
    }

    fun endFeature() {
        dynamicVars.pop()
    }

    fun startTest() {
        dynamicVars.push()
    }

    fun endTest() {
        dynamicVars.pop()
    }

    fun startScenario() {
        dynamicVars.push()
    }

    fun endScenario() {
        dynamicVars.pop()
    }

    fun startComposedStep() {
        argsVars.push()
    }

    fun endComposedStep() {
        argsVars.pop()
    }

    fun resolveIn(arg: Arg): Any? {
        // todo: remove this hack
        val escape: (String) -> String = if (arg.typeMeta is ObjectTypeMeta) {
            { StringEscapeUtils.escapeJson(it) }
        } else {
            { it }
        }

        return resolveInText(arg.content, escape)
    }

    private fun resolveInText(
        text: String?,
        escape: (String) -> String = { it }
    ): Any? {
        return resolveInText(text, escape, seenParts = emptySet())
    }

    private fun resolveInText(
        text: String?,
        escape: (String) -> String,
        seenParts: Set<String>
    ): Any? {
        if (text in seenParts) {
            throw RuntimeException("cannot resolve expression [$text]: detected cycle between variables: $seenParts")
        }

        if (text == null) {
            return null
        }

        val resolvedArgParts = mutableListOf<Any?>()

        val parts: List<FileArgPart> = ARG_PART_PARSER.parse(text)

        for (part: FileArgPart in parts) {
            val resolvedArgPartPart: Any? = when (part) {
                is FileTextArgPart -> part.text
                is FileExpressionArgPart -> {
                    if (part.text in seenParts) {
                        throw RuntimeException("cannot resolve expression [$text]: detected cycle $seenParts")
                    }

                    val expressionResult: Any? = ExpressionEvaluator.evaluate(part.text, varsContainer)
                    val escapedExpressionResult: Any? = if (expressionResult is String) {
                        escape(expressionResult)
                    } else {
                        expressionResult
                    }

                    if (escapedExpressionResult is String) {
                        resolveInText(escapedExpressionResult, escape, seenParts = seenParts + part.text)
                    } else {
                        escapedExpressionResult
                    }
                }
            }

            resolvedArgParts += resolvedArgPartPart
        }

        return when {
            resolvedArgParts.isEmpty() -> ""
            resolvedArgParts.size == 1 -> resolvedArgParts[0] // not joining to String to preserve the type
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
    fun resolveIn(text: String, escape: (String) -> String = { it }): String = resolveInText(text, escape).toString()

    fun getVariablesDetailsForDebugging(): String = formatMap(toMap())

    private fun toMap(): Map<String, Any?> {
        val result = HashMap<String, Any?>()

        val keys = getAllKeys()
        for (key in keys) {
            result[key] = get(key)
        }

        return result
    }

    private fun getAllKeys(): Set<String> {
        val result = HashSet<String>()

        result.addAll(globalVars.getKeys())
        result.addAll(dynamicVars.getKeys())
        result.addAll(argsVars.getKeys())

        return result
    }

    private fun formatMap(map: Map<String, Any?>): String = buildString {
        if (map.isEmpty()) {
            return@buildString
        }

        val biggestKeySize = map.keys.maxOf { it.length }

        val mapSortedByKeys = TreeMap(map)
        for ((key, value) in mapSortedByKeys) {
            append("[$key]".padEnd(biggestKeySize + 2))

            append(" = ")

            if (value != null) {
                append("[")
            }
            append(value.toString())
            if (value != null) {
                append("]")
            }
            append("\n")
        }
    }
}
