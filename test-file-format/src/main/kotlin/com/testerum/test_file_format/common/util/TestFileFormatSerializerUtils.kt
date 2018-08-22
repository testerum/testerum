package com.testerum.test_file_format.common.util

fun escapeMultiLineString(multiLineString: String): String
        = buildString {
            for ((index, char) in multiLineString.withIndex()) {
                when (char) {
                    '>'  -> {
                        if (index < (multiLineString.length - 1) && multiLineString[index + 1] == '>') {
                            append("""\>""")
                        } else {
                            append(""">""")
                        }
                    }
                    else -> append(char)
                }
            }
        }
