package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DataGeneratorScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DateScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.GenerateStringByRegexScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.UuidScriptingHelper
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.NashornSandboxes
import javax.script.Bindings

object ExpressionEvaluator {

    // todo: make these configurable
    private val helpers = listOf<ScriptingHelper>(
            UuidScriptingHelper,
            DateScriptingHelper,
            DataGeneratorScriptingHelper,
            GenerateStringByRegexScriptingHelper
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

        var enhancedExpression: String? = null
        try {
            enhancedExpression = enhanceExpression(expression)

            return sandbox.eval(enhancedExpression, bindings)
        } catch (e: Exception) {
            val errorMessage = buildString {
                append("failed to evaluate expression [").append(expression).append("]")

                if (enhancedExpression != null) {
                    append(", enhancedExpression=[").append(enhancedExpression).append("]")
                }
            }

            throw RuntimeException(errorMessage, e)
        }
    }

    private fun enhanceExpression(expression: String): String = buildString {
        for (helper in helpers) {
            append(helper.script)
            append("\n")
            append("\n")

        }

        append(expression)
    }

}
