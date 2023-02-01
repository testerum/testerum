package com.testerum.common_kotlin

fun <T> List<T>.withAdditional(item: T): List<T> {
    val result = arrayListOf<T>()

    result.addAll(this)
    result.add(item)

    return result
}
