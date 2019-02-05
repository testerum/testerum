package com.testerum.launcher.runner

import com.testerum.common_jdk.OsUtils
import com.testerum.launcher.config.ConfigManager
import com.testerum.launcher.config.PathsManager
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit
import java.nio.file.Path as JavaPath

class TesterumExecuter {

    private var process: Process? = null

    lateinit var serverStartedHandler: () -> Unit
    lateinit var serverPortNotAvailableHandler: () -> Unit

    fun startTesterum() {
        ProcessExecutor()
                .command(getCommand())
                .readOutput(true)
                .addListener(object : ProcessListener() {
                    override fun afterStart(newProcess: Process, executor: ProcessExecutor) {
                        process = newProcess
                    }
                })
                .redirectOutput(
                        object : LogOutputStream() {
                            override fun processLine(line: String) {
                                println(line)
                                if (line == "Testerum server started.") {
                                    serverStartedHandler()
                                }
                                if (line.contains("java.net.BindException: Address already in use")) {
                                    serverPortNotAvailableHandler()
                                }
                            }
                        }
                )
                .start()

        // make sure to stop the sub-process when closing the launcher
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                stopProcess(process)
            }
        })
    }

    private fun getCommand(): List<String> {
        val result = mutableListOf<String>()

        // java
        result += OsUtils.getJavaBinaryPath().toString()

        // options
        val config = ConfigManager.getConfig()

        result += "-Dfile.encoding=UTF8"
        result += "-Duser.timezone=GMT"
        result += "-Xmx1024m"
        result += "-Dlogback.configurationFile=${PathsManager.logConfigFilePath}"
        result += "-Dtesterum.packageDirectory=${PathsManager.testerumRootDir}"
        result += "-Dtesterum.web.httpPort=${config.port}"

        // classpath
        result += "-classpath"
        result += "${PathsManager.classpathLibDir}/*"

        // main class
        result += "com.testerum.web_backend.TesterumWebMain"

        return result
    }

    fun stopTesterum() {
        if (process != null) {
            stopProcess(process)
        }
    }

    private fun stopProcess(process: Process?) {
        val systemProcess: SystemProcess = Processes.newStandardProcess(process)

        ProcessUtil.destroyGracefullyOrForcefullyAndWait(
                systemProcess,
                2, TimeUnit.SECONDS,
                10, TimeUnit.SECONDS
        )
    }

}
