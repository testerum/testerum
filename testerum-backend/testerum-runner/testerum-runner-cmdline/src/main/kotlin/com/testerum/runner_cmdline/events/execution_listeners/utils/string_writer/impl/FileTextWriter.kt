package com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.impl

import com.testerum.common_kotlin.createDirectories
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.TextWriter
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.file.Path as JavaPath

class FileTextWriter(filePath: JavaPath): TextWriter {

    private val file: Writer

    init {
        filePath.parent.createDirectories()

        file = OutputStreamWriter(FileOutputStream(filePath.toFile()))
    }

    override fun write(text: String) {
        file.write(text)
    }

    override fun close() {
        file.close()
    }
}