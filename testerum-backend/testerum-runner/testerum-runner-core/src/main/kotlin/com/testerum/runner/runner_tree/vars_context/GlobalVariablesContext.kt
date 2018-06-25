package com.testerum.runner.runner_tree.vars_context

import com.testerum.api.test_context.test_vars.VariableNotFoundException

class GlobalVariablesContext private constructor(private val vars: Map<String, String>) {

    companion object {
        fun from(vars: Map<String, String>) = GlobalVariablesContext(HashMap(vars))
    }

    fun containsKey(name: String): Boolean = vars.containsKey(name)

    operator fun get(name: String): String {
        if (!vars.containsKey(name)) {
            throw VariableNotFoundException(name)
        }

        return vars[name]!!
    }

}
