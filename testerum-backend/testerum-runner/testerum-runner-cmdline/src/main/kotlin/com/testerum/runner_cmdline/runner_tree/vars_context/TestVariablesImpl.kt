package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum.api.test_context.test_vars.TestVariables
import javax.annotation.concurrent.NotThreadSafe

@NotThreadSafe
object TestVariablesImpl : TestVariables {

    private lateinit var variablesContext: VariablesContext

    fun setVariablesContext(variablesContext: VariablesContext) {
        TestVariablesImpl.variablesContext = variablesContext
    }

    override fun getArgsVars(): Map<String, Any?> = variablesContext.getArgsVars()

    override fun getDynamicVars(): Map<String, Any?> = variablesContext.getDynamicVars()

    override fun getGlobalVars(): Map<String, Any?> = variablesContext.getGlobalVars()

    override fun contains(name: String): Boolean
            = variablesContext.contains(name)

    override fun get(name: String): Any?
            = variablesContext[name]

    override fun getOrDefault(name: String, defaultValue: Any?): Any?
            = if (contains(name)) {
        get(name)
            } else {
                defaultValue
            }

    override fun getOrDefault(name: String, defaultValueSupplier: () -> Any?): Any?
            = if (contains(name)) {
        get(name)
            } else {
                defaultValueSupplier()
            }


    override fun set(name: String, value: Any?): Any?
            = variablesContext.setDynamicVariable(name, value)

    override fun resolveIn(text: String): String
            = variablesContext.resolveIn(text)

}
