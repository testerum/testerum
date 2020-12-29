package com.testerum.common.expression_evaluator

import com.testerum.common.expression_evaluator.bindings.VarsContainerBindings
import com.testerum.common.expression_evaluator.bindings.vars_container.CompositeVarsContainer
import com.testerum.common.expression_evaluator.bindings.vars_container.MapBasedVarsContainer
import com.testerum.common.expression_evaluator.bindings.vars_container.VarsContainer
import com.testerum.common.expression_evaluator.helpers.ScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DataGeneratorScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.DateScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.EscapeHelper
import com.testerum.common.expression_evaluator.helpers.impl.GenerateStringByRegexScriptingHelper
import com.testerum.common.expression_evaluator.helpers.impl.TestHelper
import com.testerum.common.expression_evaluator.helpers.impl.UuidScriptingHelper
import jdk.nashorn.internal.runtime.ECMAException
import javax.script.Bindings
import javax.script.ScriptEngineManager

object ExpressionEvaluator {

    private val globals: Map<String, Any?> = run {
        val result = HashMap<String, Any?>()

        // todo: make these configurable
        val helpers: List<ScriptingHelper> = listOf(
            UuidScriptingHelper,
            DateScriptingHelper,
            DataGeneratorScriptingHelper,
            GenerateStringByRegexScriptingHelper,
            EscapeHelper,
            TestHelper
        )

        for (helper in helpers) {
            for ((name, value) in helper.globalVariables) {
                result[name] = value
            }
        }

        return@run result
    }

    private val jsEngine = ScriptEngineManager().getEngineByName("nashorn")

    fun evaluate(
        expression: String,
        context: Map<String, Any?>
    ): Any? {
        return evaluate(
            expression = expression,
            varsContainer = MapBasedVarsContainer(HashMap(context))
        )
    }

    fun evaluate(
        expression: String,
        varsContainer: VarsContainer,
    ): Any? {
        val bindings: Bindings = VarsContainerBindings(
            CompositeVarsContainer().apply {
                addMap(globals)
                addContainer(varsContainer)
            }
        )

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
