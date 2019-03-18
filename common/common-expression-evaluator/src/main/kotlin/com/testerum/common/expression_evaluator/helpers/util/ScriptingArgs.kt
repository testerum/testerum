package com.testerum.common.expression_evaluator.helpers.util

class ScriptingArgs(val functionName: String,
                    val args: Array<out Any?>) {

    val size: Int
        get() = args.size

    fun requireMinimumLength(minLength: Int) {
        if (args.size < minLength) {
            throw RuntimeException("the function [$functionName] should have at least $minLength arguments, but got only ${args.size}")
        }
    }

    inline operator fun <reified R> get(index: Int): R {
        if (index < 0 || index >= args.size) {
            throw IllegalArgumentException("function [$functionName]: invalid argument index $index: should have been between 0 and ${args.size - 1}")
        }

        val arg = args[index]

        if (arg !is R) {
            throw IllegalArgumentException("function [$functionName]: invalid argument index $index: should have been between 0 and ${args.size - 1} (inclusive)")
        }

        return arg
    }

}

