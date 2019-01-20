package com.testerum.runner_cmdline.events.execution_listeners.utils.node

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner_cmdline.dirs.RunnerDirs
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.events.execution_listeners.utils.string_writer.println
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.ProcessResult
import org.zeroturnaround.exec.stream.LogOutputStream
import java.nio.file.Path as JavaPath

object RunnerNodeExecuter {

    fun executeNode(scriptFile: JavaPath,
                    vararg args: String) {
        var commandLine: List<String> = emptyList()
        try {
            // execute node
            val scriptFileName = scriptFile.fileName?.toString()
            commandLine = createCommandLine(scriptFile, args.toList())
            val processExecutor = ProcessExecutor()
                    .command(commandLine)
                    .redirectOutput(
                            object : LogOutputStream() {
                                override fun processLine(line: String) {
                                    ConsoleOutputCapturer.getOriginalTextWriter().println("[$scriptFileName] $line")
                                }
                            }
                    )
            val startTime = System.currentTimeMillis()
            val processResult: ProcessResult = processExecutor.execute()
            ConsoleOutputCapturer.getOriginalTextWriter().println("execution took ${System.currentTimeMillis() - startTime} ms")

            if (processResult.exitValue == ExitCode.RUNNER_FAILED.code) {
                ConsoleOutputCapturer.getOriginalTextWriter().println(
                        """|node process exited with code ${processResult.exitValue}
                           |commandLine = $commandLine
                           |output = [${processResult.outputUTF8()}]
                        """.trimMargin()
                )
            }
        } catch (e: Exception) {
            ConsoleOutputCapturer.getOriginalTextWriter().println(
                    """|failed to start the node process
                       |commandLine = $commandLine
                       |exception = ${e.toStringWithStacktrace()}
                    """.trimMargin()
            )
        }
    }

    private fun createCommandLine(scriptFile: JavaPath,
                                  args: List<String>): List<String> {
        val commandLine = mutableListOf<String>()

        commandLine.add(RunnerDirs.getNodeBinaryPath().toString())

        commandLine.add(scriptFile.toString())
        commandLine.addAll(args)

        return commandLine
    }

}
