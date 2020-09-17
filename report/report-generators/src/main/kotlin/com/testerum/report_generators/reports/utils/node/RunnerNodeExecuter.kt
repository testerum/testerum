package com.testerum.report_generators.reports.utils.node

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.report_generators.dirs.ReportDirs
import com.testerum.runner.exit_code.ExitCode
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
                                    println("[$scriptFileName] $line")
                                }
                            }
                    )
            val startTime = System.currentTimeMillis()
            val processResult: ProcessResult = processExecutor.execute()
            println("execution took ${System.currentTimeMillis() - startTime} ms")

            if (processResult.exitValue == ExitCode.RUNNER_FAILED.code) {
                println(
                        """|node process exited with code ${processResult.exitValue}
                           |commandLine = $commandLine
                           |output = [${processResult.outputUTF8()}]
                        """.trimMargin()
                )
            }
        } catch (e: Exception) {
            println(
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

        commandLine.add(ReportDirs.getNodeBinaryPath().toString())

        commandLine.add(scriptFile.toString())
        commandLine.addAll(args)

        return commandLine
    }

}
