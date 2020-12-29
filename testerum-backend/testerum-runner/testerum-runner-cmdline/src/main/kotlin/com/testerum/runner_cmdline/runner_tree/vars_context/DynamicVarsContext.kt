package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException

class DynamicVarsContext {

    private val stack = ArrayDeque<MutableMap<String, Any?>>()

    private fun currentLevel(): MutableMap<String, Any?> {
        return stack.lastOrNull()
            ?: throw RuntimeException("empty stack")
    }

    fun push() {
        stack.addLast(HashMap())
    }

    fun pop() {
        stack.removeLast()
    }

    fun containsKey(name: String): Boolean {
        val iterator = stack.listIterator()
        while (iterator.hasPrevious()) {
            val stackLevel = iterator.previous()

            if (stackLevel.containsKey(name)) {
                return true
            }
        }

        return false
    }

    fun getKeys(): Set<String> {
        val result = HashSet<String>()

        val iterator = stack.listIterator()
        while (iterator.hasPrevious()) {
            val stackLevel = iterator.previous()

            result.addAll(stackLevel.keys)
        }

        return result
    }

    fun get(name: String): Any? {
        val iterator = stack.listIterator()
        while (iterator.hasPrevious()) {
            val stackLevel = iterator.previous()

            if (stackLevel.containsKey(name)) {
                return stackLevel[name]
            }
        }

        throw VariableNotFoundException(name)
    }

    fun set(name: String, value: Any?) {
        currentLevel()[name] = value
    }

}
