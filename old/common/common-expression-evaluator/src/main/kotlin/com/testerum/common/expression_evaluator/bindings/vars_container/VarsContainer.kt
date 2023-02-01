package com.testerum.common.expression_evaluator.bindings.vars_container

interface VarsContainer {

    fun containsKey(name: String): Boolean
    fun get(name: String): Any?
    fun set(name: String, value: Any?): Any?

}
