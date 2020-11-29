package com.testerum.report_generators.reports.utils.console_output_capture

import com.testerum.report_generators.reports.utils.string_writer.TextPrinter
import com.testerum.report_generators.reports.utils.string_writer.impl.PrintStreamTextPrinter
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object ConsoleOutputCapturer {

    private val lock = ReentrantLock()

    private var capturingData: ConsoleOutputCapturingData? = null

    fun startCapture(owner: String) {
        lock.withLock {
            val capturingData = this.capturingData
            if (capturingData != null) {
                throw IllegalStateException("[$owner] cannot capture the output because it's already captured by [${capturingData.owner}]")
            }

            this.capturingData = ConsoleOutputCapturingData.startCapture(owner)

            Runtime.getRuntime().addShutdownHook(Thread {
                cleanup()
            })
        }
    }

    fun getOriginalTextWriter(): TextPrinter {
        val capturingData = this.capturingData
                ?: throw IllegalStateException("output not yet captured; did you forget to call startCapture()?")

        return capturingData.getOriginalTextWriter()
    }

    fun drainCapturedText(): String {
        lock.withLock {
            val capturingData = this.capturingData
                    ?: throw IllegalStateException("output not yet captured; did you forget to call startCapture()?")

            return capturingData.drainCapturedText()
        }
    }

    fun stopCapture() {
        lock.withLock {
            capturingData?.restoreOriginalOutput()

            capturingData == null
        }
    }

    private fun cleanup() {
        val remainingConsoleCapturedText: String = drainCapturedText()
        stopCapture()
        println(remainingConsoleCapturedText)
    }
}

private class ConsoleOutputCapturingData(val owner: String) {

    companion object {
        private val CHARSET = StandardCharsets.UTF_8

        fun startCapture(owner: String): ConsoleOutputCapturingData {
            val newCapturingData = ConsoleOutputCapturingData(owner)

            System.setOut(newCapturingData.capturingPrintStream)
            System.setErr(newCapturingData.capturingPrintStream)

            return newCapturingData
        }
    }
    private val originalStdout: PrintStream = System.out
    private val originalStderr: PrintStream = System.err
    private val originalTextWriter = PrintStreamTextPrinter(originalStdout)

    private val capturingOutputStream = ByteArrayOutputStream()
    private val capturingPrintStream: PrintStream = PrintStream(capturingOutputStream, false, CHARSET.name())

    fun getOriginalTextWriter() = originalTextWriter

    fun drainCapturedText(): String {
        capturingPrintStream.flush()

        val result = capturingOutputStream.toString(CHARSET.name())

        capturingOutputStream.reset()

        return result
    }

    fun restoreOriginalOutput() {
        System.setOut(originalStdout)
        System.setErr(originalStderr)
    }

}
