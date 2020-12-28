package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import java.util.Collections

class ArgsContext(val parent: ArgsContext?) {

    private val vars = HashMap<String, Any?>()

    fun containsKey(name: String): Boolean = vars.containsKey(name)

    fun get(name: String): Any? {
        if (!vars.containsKey(name)) {
            throw VariableNotFoundException(name)
        }

        return vars[name]
    }

    fun set(name: String, value: Any?) {
        vars[name] = value
    }

    fun toMap(): Map<String, Any?> = Collections.unmodifiableMap(vars)
}
