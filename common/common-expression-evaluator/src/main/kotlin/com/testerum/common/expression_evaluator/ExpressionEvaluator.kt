package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.*
import jdk.nashorn.internal.runtime.ECMAException
import javax.script.Bindings
import javax.script.ScriptEngineManager

object ExpressionEvaluator {

    // todo: make these configurable
    private val helpers: List<ScriptingHelper> = listOf(
            UuidScriptingHelper,
            DateScriptingHelper,
            DataGeneratorScriptingHelper,
            GenerateStringByRegexScriptingHelper,
            EscapeHelper,
            TestHelper
    )

    private val jsEngine = ScriptEngineManager().getEngineByName("nashorn")

    fun evaluate(expression: String,
                 context: Map<String, Any?>): Any? {
        val bindings: Bindings = jsEngine.createBindings().apply {
            for (helper in helpers) {
                val globalVariables = helper.globalVariables

                for ((name, value) in globalVariables) {
                    put(name, value)
                }
            }

            putAll(context)
            put("vars", context)
        }

        try {
            return jsEngine.eval(expression, bindings)
        } catch (e: Exception) {
            val ecmaException = e.selfOrCauseOfType<ECMAException>()
            val originalErrorMessage = if (ecmaException != null) {
                ecmaException.message
            } else {
                e.message
            }

            throw RuntimeException("failed to evaluate expression {{$expression}}\n$originalErrorMessage", e)
        }
    }

    private inline fun <reified T> Throwable.selfOrCauseOfType(): T? {
        var exception: Throwable? = this

        do {
            if (exception is T) {
                return exception
            }

            exception = exception?.cause
        } while (exception != null)

        return null
    }
}
