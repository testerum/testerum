package com.testerum.runner_cmdline.runner_api_services

import com.testerum.common.expression_evaluator.ExpressionEvaluator
import com.testerum.common.expression_evaluator.bindings.vars_container.CompositeVarsContainer
import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.script_executer.ScriptExecuter

class ScriptExecuterImpl(private val variablesContext: VariablesContext) : ScriptExecuter {

    override fun executeScript(script: String): Any? {
        return executeScript(script, emptyMap())
    }

    override fun executeScript(
        script: String,
        context: Map<String, Any?>
    ): Any? {
        val varsContainer = CompositeVarsContainer().apply {
            addContainer(variablesContext.varsContainer)
            addMap(
                mapOf(
                    "testContext" to TesterumServiceLocator.getTestContext(),
                    "testLogger" to TesterumServiceLocator.getTesterumLogger(),
                    "testVariables" to TesterumServiceLocator.getTestVariables(),
                )
            )

            addMap(context)
        }

        return ExpressionEvaluator.evaluate(script, varsContainer)
    }

}
