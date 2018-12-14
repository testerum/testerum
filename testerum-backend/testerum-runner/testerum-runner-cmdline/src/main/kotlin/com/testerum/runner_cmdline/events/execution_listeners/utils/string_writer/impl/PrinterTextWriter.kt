package com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.impl

import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.TextPrinter
import java.io.PrintStream

class PrinterTextWriter(private val wrappedPrintStream: PrintStream) : TextPrinter {

    override fun print(text: String) {
        wrappedPrintStream.print(text)
    }

    override fun close() { }

}
