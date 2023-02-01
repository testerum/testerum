package com.testerum.common.expression_evaluator.bindings

import com.testerum.common.expression_evaluator.bindings.vars_container.VarsContainer
import javax.script.Bindings

class VarsContainerBindings(private val varsContainer: VarsContainer) : Bindings {

    override fun clear() {
        throw UnsupportedOperationException("clear not supported")
    }

    override fun put(key: String, value: Any?): Any? {
        return varsContainer.set(key, value)
    }

    override fun putAll(from: Map<out String, Any>) {
        for ((key, value) in from) {
            put(key, value)
        }
    }

    override fun remove(key: String?): Any? {
        throw UnsupportedOperationException("remove not supported")
    }

    override fun containsKey(key: String): Boolean {
        return varsContainer.containsKey(key)
    }

    override val size: Int
        get() = throw UnsupportedOperationException("size not supported")


    override fun containsValue(value: Any?): Boolean {
        throw UnsupportedOperationException("containsValue not supported")
    }

    override fun get(key: String): Any? {
        return varsContainer.get(key)
    }

    override fun isEmpty(): Boolean {
        throw UnsupportedOperationException("isEmpty not supported")
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>
        get() = throw UnsupportedOperationException("entries not supported")

    override val keys: MutableSet<String>
        get() = throw UnsupportedOperationException("keys not supported")

    override val values: MutableCollection<Any>
        get() = throw UnsupportedOperationException("values not supported")
}
