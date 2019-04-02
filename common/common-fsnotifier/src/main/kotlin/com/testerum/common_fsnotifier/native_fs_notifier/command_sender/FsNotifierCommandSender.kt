package com.testerum.common_fsnotifier.native_fs_notifier.command_sender

import com.testerum.common_kotlin.canonicalize
import org.slf4j.LoggerFactory
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.nio.file.Path as JavaPath

class FsNotifierCommandSender(outputStream: OutputStream) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FsNotifierCommandSender::class.java)

        private const val COMMAND_SET_ROOTS = "ROOTS"
        private const val COMMAND_EXIT = "EXIT"
    }

    private val writer: Writer = OutputStreamWriter(outputStream, Charsets.UTF_8)

    fun setRoots(recursiveRoots: List<JavaPath>,
                 flatRoots: List<JavaPath>) {
        writer.sendLine(COMMAND_SET_ROOTS)

        for (recursiveRoot in recursiveRoots) {
            writer.sendLine(recursiveRoot.canonicalize().toString())
        }

        for (flatRoot in flatRoots) {
            writer.sendLine("|" + flatRoot.canonicalize().toString())
        }

        writer.sendLine("#")
    }

    fun exit() {
        writer.sendLine(COMMAND_EXIT)
    }

    private fun Writer.sendLine(line: String) {
        LOG.debug("[fsnotifier-input] $line")

        write(line)
        write("\n")
        flush()
    }

}
