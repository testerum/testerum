package com.testerum.launcher.runner

import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit

class TesterumRunner {
    val EXECUTABLE_FILE_VM_PROP = "testerumExeFileLocation"

    var processExecutor: ProcessExecutor? = null;
    var process: Process? = null;

    fun startTesterum() {
        processExecutor = ProcessExecutor()
                .command(getExeFilePath())
                .readOutput(true)
                .addListener(object : ProcessListener() {
                    override fun afterStart(newProcess: Process, executor: ProcessExecutor) {
                        process = newProcess;
                        processExecutor = executor;
                    }

                    override fun afterStop(process: Process?) {
                        System.exit(1);
                    }
                })
                .redirectOutput(
                        object : LogOutputStream() {
                            override fun processLine(line: String) {
                                println(line)
                            }
                        }
                )
    }

    fun stopTesterum() {
        if (process != null) {
            val systemProcess: SystemProcess = Processes.newStandardProcess(process)

            ProcessUtil.destroyGracefullyOrForcefullyAndWait(
                    systemProcess,
                    2, TimeUnit.SECONDS,
                    10, TimeUnit.SECONDS
            )
        }
    }

    private fun getExeFilePath(): String {
        return System.getProperty(EXECUTABLE_FILE_VM_PROP)
                ?: throw Exception("The VM property [${EXECUTABLE_FILE_VM_PROP}] was not specified")
    }
}