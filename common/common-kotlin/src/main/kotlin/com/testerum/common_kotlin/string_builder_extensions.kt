package com.testerum.common_kotlin

fun StringBuilder.indent(indent: Int, indentPerLevel: Int = 4): StringBuilder {
    for (i in 1..indent) {
        append(" ".repeat(indentPerLevel))
    }

    return this
}
