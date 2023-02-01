package com.testerum.runner_cmdline

import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.model.runner.tree.id.RunnerIdCreator
import com.testerum.report_generators.reports.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner_cmdline.cmdline.exiter.Exiter
import com.testerum.runner_cmdline.cmdline.params.CmdlineParamsParser
import com.testerum.runner_cmdline.cmdline.params.model.HelpRequested
import com.testerum.runner_cmdline.cmdline.params.model.RunCmdlineParams
import com.testerum.runner_cmdline.cmdline.params.model.VersionRequested
import com.testerum.runner_cmdline.help.RunnerHelpInfoService
import com.testerum.runner_cmdline.module_di.RunnerModuleBootstrapper
import com.testerum.runner_cmdline.module_di.TesterumRunnerLoggingConfigurator
import com.testerum.runner_cmdline.version.RunnerVersionInfoService
import java.time.LocalDateTime

object TesterumRunner {

    @JvmStatic
    fun main(args: Array<String>) {
        val stopWatch = StopWatch.start()

        TesterumRunnerLoggingConfigurator.configureLogging()
        println(TesterumBanner.BANNER)

        ConsoleOutputCapturer.startCapture("main")

        val cmdlineParams: RunCmdlineParams = getCmdlineParams(args)
        println("cmdlineParams = $cmdlineParams")

        val bootstrapper = RunnerModuleBootstrapper(cmdlineParams, stopWatch)
        val exitCode: ExitCode = bootstrapper.context.use {
            val appExitCode: ExitCode
            try {
                appExitCode = bootstrapper.runnerModuleFactory.runnerApplication.execute(cmdlineParams)
            } finally {
                val remainingConsoleCapturedText: String = ConsoleOutputCapturer.drainCapturedText()

                ConsoleOutputCapturer.stopCapture()

                try {
                    for (line in remainingConsoleCapturedText.lines()) {
                        bootstrapper.runnerModuleFactory.eventsService.logEvent(
                            TextLogEvent(
                                time = LocalDateTime.now(),
                                eventKey = RunnerIdCreator.getRootId(),
                                logLevel = LogLevel.INFO,
                                message = line,
                                exceptionDetail = null
                            )
                        )
                    }
                } catch (e: Exception) {
                    // if we failed to notify listeners, show output to console, so we don't lose it
                    println("An error occurred while trying to notify event listeners of remaining logs:")
                    e.printStackTrace()

                    println()
                    println("Remaining logs:")
                    println("----------------------------------------(start)----------------------------------------")
                    println(remainingConsoleCapturedText)
                    println("----------------------------------------( end )----------------------------------------")
                }
            }

            appExitCode
        }

        Exiter.exit(exitCode)
    }

    private fun getCmdlineParams(args: Array<out String>): RunCmdlineParams {
        when (val cmdlineParams = CmdlineParamsParser.parse(args.toList())) {
            is HelpRequested -> {
                println(RunnerHelpInfoService.getUsageHelp())
                Exiter.exit(ExitCode.OK)
            }

            is VersionRequested -> {
                println(RunnerVersionInfoService.getFormattedVersionProperties())
                Exiter.exit(ExitCode.OK)
            }

            is RunCmdlineParams -> {
                return cmdlineParams
            }
        }
    }

}

