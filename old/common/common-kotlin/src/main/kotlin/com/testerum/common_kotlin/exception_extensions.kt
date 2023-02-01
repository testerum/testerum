package com.testerum.common_kotlin

val Throwable.rootCause: Throwable
    get() {
        var root: Throwable = this
        var cause = root.cause

        while (cause != null) {
            root = cause
            cause = cause.cause
        }

        return root
    }

