package com.testerum.test_file_format.common.util

fun escapeMultiLineString(multiLineString: String): String = multiLineString.replace(">", "\\>")
