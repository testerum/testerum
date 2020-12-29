package com.testerum.common.expression_evaluator.bindings.vars_container

class MapBasedVarsContainer(private val map: MutableMap<String, Any?>) : VarsContainer {

    override fun containsKey(name: String): Boolean = map.containsKey(name)

    override fun get(name: String): Any? = map[name]

    override fun set(name: String, value: Any?): Any? {
        return map.put(name, value)
    }

}
