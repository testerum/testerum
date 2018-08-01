package com.testerum.common_kotlin

fun <R> runWithThreadContextClassLoader(classLoader: ClassLoader, block: () -> R): R {
    val originalThreadContextClassLoader = Thread.currentThread().contextClassLoader

    Thread.currentThread().contextClassLoader = classLoader
    try {
        return block()
    } finally {
        Thread.currentThread().contextClassLoader = originalThreadContextClassLoader
    }
}