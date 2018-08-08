package com.testerum.common_jdk.stopwatch

import java.util.concurrent.TimeUnit

@Suppress("MemberVisibilityCanBePrivate", "unused")
class StopWatch(private val startTimeNanos: Long) {

    companion object {
        fun start(): StopWatch = StopWatch(System.nanoTime())
    }

    fun elapsedMillis(): Long = TimeUnit.NANOSECONDS.toMillis(elapsedNanos())

    fun elapsedNanos(): Long = (System.nanoTime() - startTimeNanos)

}