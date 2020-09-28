package com.testerum.report_generators.reports.utils.string_writer.impl

import com.testerum.report_generators.reports.utils.string_writer.TextPrinter
import java.io.PrintStream

class PrintStreamTextPrinter(private val wrappedPrintStream: PrintStream) : TextPrinter {

    override fun print(text: String) {
        wrappedPrintStream.print(text)
    }

    override fun close() { }

}
