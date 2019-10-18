package com.testerum.web_backend.services.demo

import com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter.FsNotifierEventInterpreter
import com.testerum.common_jdk.OsUtils
import com.testerum.model.home.Project
import com.testerum.settings.TesterumDirs
import com.testerum.web_backend.services.project.ProjectFrontendService
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.listener.ProcessListener
import org.zeroturnaround.exec.stream.LogOutputStream
import org.zeroturnaround.process.ProcessUtil
import org.zeroturnaround.process.Processes
import org.zeroturnaround.process.SystemProcess
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class DemoService (private val testerumDirs: TesterumDirs,
                   private val projectFrontendService: ProjectFrontendService) {
    companion object {
        private val LOG = LoggerFactory.getLogger(FsNotifierEventInterpreter::class.java)
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
        val scriptFileExtension = if (OsUtils.IS_WINDOWS) ".bat" else ".sh"
        val scriptFileName = "start" + scriptFileExtension
        val scriptFilePath: Path = testerumDirs.getDemoDir().resolve(scriptFileName)

        while (demoAppProcess != null && demoAppProcess!!.isAlive) {
            stopDemoApp();
            Thread.sleep(1000)
        }

        ProcessExecutor()
                .command(getCommand(scriptFilePath))
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
                .start()
    }


    private fun getCommand(scriptFilePath: Path ): List<String> {
        val result = mutableListOf<String>()

        result += scriptFilePath.toString()

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

    fun getDemoProject(): Project {
        return projectFrontendService.openProject(
                testerumDirs.getDemoTestsDir().toString()
        )
    }
}
