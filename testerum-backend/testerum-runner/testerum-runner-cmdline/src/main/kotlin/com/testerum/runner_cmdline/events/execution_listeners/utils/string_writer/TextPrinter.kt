package com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer

import java.io.Closeable

interface TextPrinter : Closeable {

    fun print(text: String)

}

fun TextPrinter.println(text: String) = print("$text\n")
