package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum.api.test_context.test_vars.VariableNotFoundException
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
class DynamicVariablesContext {

    private val vars = mutableMapOf<String, Any?>()

    fun containsKey(name: String): Boolean = vars.containsKey(name)

    operator fun get(name: String): Any? {
        if (!vars.containsKey(name)) {
            throw VariableNotFoundException(name)
        }

        return vars[name]
    }

    operator fun set(name: String, value: Any?): Any? {
        return vars.put(name, value)
    }

    fun toMap(): Map<String, Any?> = vars

}