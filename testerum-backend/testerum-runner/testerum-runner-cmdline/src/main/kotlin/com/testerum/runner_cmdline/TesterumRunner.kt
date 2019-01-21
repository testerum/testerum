package com.testerum.runner_cmdline

import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.runner.events.model.TextLogEvent
import com.testerum.runner.events.model.log_level.LogLevel
import com.testerum.runner.events.model.position.EventKey
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner_cmdline.cmdline.exiter.Exiter
import com.testerum.runner_cmdline.cmdline.params.CmdlineParamsParser
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserVersionHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.events.execution_listeners.utils.console_output_capture.ConsoleOutputCapturer
import com.testerum.runner_cmdline.module_di.RunnerModuleBootstrapper
import com.testerum.runner_cmdline.module_di.TesterumRunnerLoggingConfigurator
import com.testerum.runner_cmdline.version.RunnerVersionInfoService
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD_OFF
import org.fusesource.jansi.AnsiConsole
import java.time.LocalDateTime

object TesterumRunner {

    @JvmStatic
    fun main(args: Array<String>) {
        val stopWatch = StopWatch.start()

        AnsiConsole.systemInstall()
        ConsoleOutputCapturer.startCapture("main")

        TesterumRunnerLoggingConfigurator.configureLogging()
        println(TesterumBanner.BANNER)

        val cmdlineParams: CmdlineParams = getCmdlineParams(args)
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
                                        eventKey = EventKey.LOG_EVENT_KEY,
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

    private fun getCmdlineParams(args: Array<out String>): CmdlineParams {
        return try {
            System.setProperty("picocli.useSimplifiedAtFiles", "true")
            CmdlineParamsParser.parse(*args)
        } catch (e: CmdlineParamsParserHelpRequestedException) {
            println(e.usageHelp)

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserVersionHelpRequestedException) {
            println(
                    RunnerVersionInfoService.getFormattedVersionProperties()
            )

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserParsingException) {
            println(
                    "${Ansi.ansi().fgBrightRed()}${Ansi.ansi().a(INTENSITY_BOLD)}" +
                    "ERROR: ${e.errorMessage}" +
                    "${Ansi.ansi().a(INTENSITY_BOLD_OFF)}${Ansi.ansi().fgDefault()}" +
                    "\n"
            )
            println(e.usageHelp)

            Exiter.exit(ExitCode.RUNNER_FAILED)
        }
    }

}

