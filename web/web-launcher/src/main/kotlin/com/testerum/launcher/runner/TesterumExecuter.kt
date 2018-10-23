package com.testerum.launcher.runner

import com.testerum.common_jdk.OsUtils
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.nio.file.Path as JavaPath

class TesterumExecuter {

    companion object {
        private val TESTERUM_ROOT_DIR_SYSTEM_PROP = "testerumRootDir"
    }

    private var process: Process? = null

    fun startTesterum() {
        ProcessExecutor()
                .command(getCommand())
                .readOutput(true)
                .addListener(object : ProcessListener() {
                    override fun afterStart(newProcess: Process, executor: ProcessExecutor) {
                        process = newProcess
                    }

                    override fun afterStop(process: Process?) {
                        System.exit(1)
                    }
                })
                .redirectOutput(
                        object : LogOutputStream() {
                            override fun processLine(line: String) {
                                println(line)
                            }
                        }
                )
                .start()
    }

    private fun getCommand(): List<String> {
        val result = mutableListOf<String>()

        // interpreter
        if (OsUtils.IS_WINDOWS) {
            result += "cmd.exe"
            result += "/C"
        }

        // shell script
        val testerumRootDir: JavaPath = getTesterumRootDir()
        val testerumStartShellScript: JavaPath = if (OsUtils.IS_WINDOWS) {
            testerumRootDir.resolve("start.bat")
        } else {
            testerumRootDir.resolve("start.sh")
        }
        result += testerumStartShellScript.toAbsolutePath().normalize().toString()

        return result
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

    private fun getTesterumRootDir(): JavaPath {
        val sysPropValue = System.getProperty(TESTERUM_ROOT_DIR_SYSTEM_PROP)
                ?: throw Exception("The system property [$TESTERUM_ROOT_DIR_SYSTEM_PROP] was not specified")

        return Paths.get(sysPropValue).toAbsolutePath().normalize()
    }
}