package com.testerum.common.expression_evaluator

object ExpressionEvaluator {

    /**
     * @throws UnknownVariableException
     */
    fun evaluate(expression: String,
                 context: Map<String, Any>): Any {
        // for now, the expression can be a single variable reference
        // later, we can use a Java script engine instead
        val variableName = expression.trim()

        return context[variableName]
                ?: throw UnknownVariableException("variable [$variableName] cannot be found in the context")
    }

}