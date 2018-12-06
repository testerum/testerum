package com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.impl

import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.TextWriter
import java.io.PrintStream

class PrinterTextWriter(private val wrappedPrintStream: PrintStream) : TextWriter {

    override fun write(text: String) {
        wrappedPrintStream.print(text)
    }

    override fun close() { }

}