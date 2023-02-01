package com.testerum.common.expression_evaluator.bindings.vars_container

class CompositeVarsContainer : VarsContainer {

    private val containers = mutableListOf<VarsContainer>()

    fun addContainer(container: VarsContainer) {
        if (container == this) {
            throw IllegalArgumentException("cannot add a container to itself")
        }
        containers += container
    }

    fun addMap(map: Map<String, Any?>) {
        val mutableMap = if (map is MutableMap) {
            map
        } else {
            HashMap(map)
        }

        containers += MapBasedVarsContainer(mutableMap)
    }

    override fun containsKey(name: String): Boolean {
        for (container in containers) {
            if (container.containsKey(name)) {
                return true
            }
        }

        return false
    }

    override fun get(name: String): Any? {
        for (container in containers) {
            if (container.containsKey(name)) {
                return container.get(name)
            }
        }

        return null
    }

    override fun set(name: String, value: Any?): Any? {
        if (containers.isEmpty()) {
            throw IllegalStateException("cannot set, because there are no containers available")
        }

        return containers.last().set(name, value)
    }
}
