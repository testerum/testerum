package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import java.util.Collections

class GlobalVarsContext private constructor(private val vars: Map<String, String>) {

    companion object {
        fun from(vars: Map<String, String>) = GlobalVarsContext(HashMap(vars))
    }

    fun containsKey(name: String): Boolean = vars.containsKey(name)

    fun getKeys(): Set<String> {
        return vars.keys
    }

    fun get(name: String): String {
        if (!vars.containsKey(name)) {
            throw VariableNotFoundException(name)
        }

        return vars[name]!!
    }

    fun toMap(): Map<String, Any?> = Collections.unmodifiableMap(vars)

}
