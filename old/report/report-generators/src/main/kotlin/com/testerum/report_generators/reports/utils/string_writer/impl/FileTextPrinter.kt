package com.testerum.report_generators.reports.utils.string_writer.impl

import com.testerum.common_kotlin.createDirectories
import com.testerum.report_generators.reports.utils.string_writer.TextPrinter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.file.Path as JavaPath

class FileTextPrinter(filePath: JavaPath): TextPrinter {

    private val file: Writer

    init {
        filePath.parent.createDirectories()

        file = OutputStreamWriter(FileOutputStream(filePath.toFile()))
    }

    override fun print(text: String) {
        file.write(text)
    }

    override fun close() {
        file.close()
    }
}
