package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DataGeneratorScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DateScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.EscapeHelper
import com.testerum.common.expression_evaluator.helpers.impl.GenerateStringByRegexScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.UuidScriptingHelper
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.NashornSandboxes
import jdk.nashorn.internal.runtime.ECMAException
import javax.script.Bindings

object ExpressionEvaluator {

    // todo: make these configurable
    private val helpers: List<ScriptingHelper> = listOf(
            UuidScriptingHelper,
            DateScriptingHelper,
            DataGeneratorScriptingHelper,
            GenerateStringByRegexScriptingHelper,
            EscapeHelper
    )

    private val sandbox: NashornSandbox = NashornSandboxes.create().apply {
        allowPrintFunctions(true)

        disallowAllClasses()

        for (helper in helpers) {
            val globalVariables = helper.globalVariables

            for ((name, value) in globalVariables) {
                inject(name, value)
            }
        }
    }

    fun evaluate(expression: String,
                 context: Map<String, Any?>): Any? {
        val bindings: Bindings = sandbox.createBindings().apply {
            putAll(context)
            put("vars", context)
        }

        try {
            return sandbox.eval(expression, bindings)
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
