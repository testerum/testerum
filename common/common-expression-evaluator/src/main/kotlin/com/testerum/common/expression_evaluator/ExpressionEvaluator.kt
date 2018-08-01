package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.UuidScriptingHelper
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.NashornSandboxes
import javax.script.Bindings

object ExpressionEvaluator {

    // todo: make these configurable
    private val helpers = listOf<ScriptingHelper>(
            UuidScriptingHelper
    )

    private val sandbox: NashornSandbox = NashornSandboxes.create().apply {
        allowPrintFunctions(true)

        disallowAllClasses()

        for (helper in helpers) {
            for (allowedClass in helper.allowedClasses) {
                allow(allowedClass)
            }
        }
    }

    fun evaluate(expression: String,
                 context: Map<String, Any?>): Any? {
        val bindings: Bindings = sandbox.createBindings().apply {
            putAll(context)
        }

        try {
            val enhancedExpression = enhanceExpression(expression)

            return sandbox.eval(enhancedExpression, bindings)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun enhanceExpression(expression: String): String = buildString {
        for (helper in helpers) {
            append(helper.script)
            append("\n")
            append("\n")

            append(expression)
        }
    }

}
