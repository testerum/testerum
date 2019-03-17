package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DataGeneratorScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DateScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.GenerateStringByRegexScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.UuidScriptingHelper
import delight.nashornsandbox.NashornSandbox
import delight.nashornsandbox.NashornSandboxes
import jdk.nashorn.internal.runtime.ECMAException
import org.slf4j.LoggerFactory
import javax.script.Bindings

object ExpressionEvaluator {

    private val LOG = LoggerFactory.getLogger(ExpressionEvaluator::class.java)

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
            put("vars", context)
        }

        var enhancedExpression: String? = null
        try {
            // todo: use sandbox.inject() for helpers
            enhancedExpression = enhanceExpression(expression)

            return sandbox.eval(enhancedExpression, bindings)
        } catch (e: Exception) {
            val ecmaException = e.selfOrCauseOfType<ECMAException>()
            val errorMessage = if (ecmaException != null) {
                ecmaException.message
            } else {
                e.message
            }

            LOG.info(
                    buildString {
                        append("failed to evaluate expression [").append(expression).append("]")

                        if (enhancedExpression != null) {
                            append(", enhancedExpression=[\n").append(enhancedExpression).append("\n]")
                        }
                    }
            )

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
