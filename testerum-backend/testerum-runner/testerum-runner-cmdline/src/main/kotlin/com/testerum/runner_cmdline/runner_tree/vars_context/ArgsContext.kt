package com.testerum.runner_cmdline.runner_tree.vars_context

import com.testerum_api.testerum_steps_api.test_context.test_vars.VariableNotFoundException

class ArgsContext {

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
        if (stack.isEmpty()) {
            return false
        }

        return currentLevel().containsKey(name)
    }

    fun getKeys(): Set<String> {
        return currentLevel().keys
    }

    fun get(name: String): Any? {
        val currentLevel = currentLevel()
        if (!currentLevel.containsKey(name)) {
            throw VariableNotFoundException(name)
        }

        return currentLevel[name]
    }

    fun set(name: String, value: Any?) {
        currentLevel()[name] = value
    }

    fun overwriteArgAtAllLevels(name: String, value: Any?) {
        val iterator = stack.listIterator()
        while (iterator.hasPrevious()) {
            val stackLevel = iterator.previous()

            if (stackLevel.containsKey(name)) {
                stackLevel[name] = value
            }
        }
    }

}
