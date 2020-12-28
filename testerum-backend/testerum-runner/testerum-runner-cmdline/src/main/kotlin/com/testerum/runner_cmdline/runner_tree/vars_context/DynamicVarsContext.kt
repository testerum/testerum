package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException
import java.util.Collections

class DynamicVarsContext(val parent: DynamicVarsContext?) {

    private val vars = HashMap<String, Any?>()

    fun containsKey(name: String): Boolean {
        var current: DynamicVarsContext? = this
        while (current != null) {
            if (current.vars.containsKey(name)) {
                return true
            }

            current = current.parent
        }

        return false
    }

    fun get(name: String): Any? {
        var current: DynamicVarsContext? = this
        while (current != null) {
            if (current.vars.containsKey(name)) {
                return current.vars[name]
            }

            current = current.parent
        }

        throw VariableNotFoundException(name)
    }

    fun set(name: String, value: Any?): Any? {
        return vars.put(name, value)
    }

    fun toMap(): Map<String, Any?> = Collections.unmodifiableMap(vars)

}
