package com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer

import java.io.Closeable

interface TextWriter : Closeable {

    fun write(text: String)

}