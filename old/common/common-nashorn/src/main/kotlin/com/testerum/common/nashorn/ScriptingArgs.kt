package com.testerum.common.nashorn

class ScriptingArgs(val functionName: String,
                    val args: Array<out Any?>) {

    val size: Int
        get() = args.size

    fun requireLength(length: Int) {
        if (args.size != length) {
            throw RuntimeException("the function [$functionName] should have $length argument(s), but got ${args.size} instead")
        }
    }

    fun requireMinimumLength(minLength: Int) {
        if (args.size < minLength) {
            throw RuntimeException("the function [$functionName] should have at least $minLength arguments, but got only ${args.size}")
        }
    }

    inline operator fun <reified R> get(index: Int): R {
        if (index < 0 || index >= args.size) {
            throw IllegalArgumentException("function [$functionName]: invalid argument at index $index: should have been between 0 and ${args.size - 1}")
        }

        val arg = args[index]

        if (arg !is R) {
            throw IllegalArgumentException("function [$functionName]: invalid argument at index $index: should have been of type [${R::class.java.name}], but is of type [${arg?.javaClass?.name}]")
        }

        return arg
    }

}

