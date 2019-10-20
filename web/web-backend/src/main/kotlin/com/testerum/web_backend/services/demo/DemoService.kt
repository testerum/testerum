package com.testerum.web_backend.services.demo

import com.testerum.common_jdk.OsUtils
import com.testerum.settings.TesterumDirs
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.util.concurrent.TimeUnit

class DemoService(private val testerumDirs: TesterumDirs) {

    companion object {
        private val LOG = LoggerFactory.getLogger(DemoService::class.java)
    }

    private var demoAppProcess: Process? = null

    fun copyDemoFilesFromInstallDirToUserSettingsDir() {

        FileUtils.copyDirectory(
                testerumDirs.getDemoDir().resolve("tests").toFile(),
                testerumDirs.getDemoTestsDir().toFile()
        )
    }

    fun startDemoApp() {

        LOG.debug("Starting DemoApp")

        while (demoAppProcess != null && demoAppProcess!!.isAlive) {
            stopDemoApp();
            Thread.sleep(1000)
        }

        ProcessExecutor()
                .command(getCommand())
                .readOutput(true)
                .addListener(object : ProcessListener() {
                    override fun afterStart(newProcess: Process, executor: ProcessExecutor) {
                        demoAppProcess = newProcess
                    }
                })
                .redirectOutput(
                        object : LogOutputStream() {
                            override fun processLine(line: String) {
                                LOG.debug("DemoApp Log: ${line}")
                            }
                        }
                )
                .destroyOnExit()
                .start()
    }


    private fun getCommand(): List<String> {
        val result = mutableListOf<String>()

        // java
        result += OsUtils.getJavaBinaryPath().toString()

        result += "-Dfile.encoding=UTF8"
        result += "-Duser.timezone=GMT"
        result += "-Xmx1024m"
        result += "-jar"
        result += "${testerumDirs.getDemoDir()}\\lib\\demo-spring-petclinic.war"

        return result
    }


    fun stopDemoApp() {
        if (demoAppProcess != null) {
            stopProcess(demoAppProcess)
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
