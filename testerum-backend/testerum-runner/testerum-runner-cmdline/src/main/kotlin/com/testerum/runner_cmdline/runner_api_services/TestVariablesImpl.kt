package com.testerum.runner_cmdline.runner_api_services

import com.testerum.runner_cmdline.runner_tree.vars_context.VariablesContext
import com.testerum_api.testerum_steps_api.test_context.test_vars.TestVariables

class TestVariablesImpl(private val variablesContext: VariablesContext) : TestVariables {

//    override fun getArgsVars(): Map<String, Any?> = variablesContext.getArgsVars()
//
//    override fun getDynamicVars(): Map<String, Any?> = variablesContext.getDynamicVars()
//
//    override fun getGlobalVars(): Map<String, Any?> = variablesContext.getGlobalVars()
//
//    override fun toMap(): Map<String, Any?> = variablesContext.toMap()

    override fun contains(name: String): Boolean = variablesContext.containsKey(name)

    override fun get(name: String): Any? = variablesContext.get(name)

    override fun getOrDefault(name: String, defaultValue: Any?): Any? = if (contains(name)) {
        get(name)
    } else {
        defaultValue
    }

    override fun getOrDefault(name: String, defaultValueSupplier: () -> Any?): Any? = if (contains(name)) {
        get(name)
    } else {
        defaultValueSupplier()
    }


    override fun set(name: String, value: Any?): Any? = variablesContext.set(name, value)

    override fun resolveIn(text: String): String = variablesContext.resolveIn(text)

    override fun getVariablesDetailsForDebugging(): String = variablesContext.getVariablesDetailsForDebugging()
}
