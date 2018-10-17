package com.testerum.common_crypto.string_obfuscator

import org.apache.commons.lang3.StringEscapeUtils

fun main(args: Array<String>) {
    val textToObfuscate = "text to obfuscate"

    val obfuscatedText = StringObfuscator.obfuscate(textToObfuscate)

    val chunks = obfuscatedText.chunked(80)

    val textToPrint = buildString {
        append("StringObfuscator.deobfuscate(")

        val multiline = chunks.size > 1
        if (multiline) {
            append("\n")
        }
        var first = true
        for (chunk in chunks) {
            if (first) {
                first = false
            } else {
                append(" +\n")
            }

            if (multiline) {
                append("    ")
            }

            append("\"")
            append(StringEscapeUtils.escapeJava(chunk))
            append("\"")
        }
        if (multiline) {
            append("\n")
        }

        append(")")
    }

    println(textToPrint)
}