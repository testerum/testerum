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

        ConsoleOutputCapturer.startCapture("main")

        TesterumRunnerLoggingConfigurator.configureLogging()
        AnsiConsole.systemInstall()
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

                for (line in remainingConsoleCapturedText.lines()) {
                    bootstrapper.runnerModuleFactory.eventsService.logEvent(
                            TextLogEvent(
                                    time = LocalDateTime.now(),
                                    eventKey = EventKey.LOG_EVENT_KEY,
                                    logLevel = LogLevel.INFO,
                                    message = line
                            )
                    )
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
            consolePrintln(e.usageHelp)

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserVersionHelpRequestedException) {
            consolePrintln(
                    RunnerVersionInfoService.getFormattedVersionProperties()
            )

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserParsingException) {
            consolePrintln(
                    "${Ansi.ansi().fgBrightRed()}${Ansi.ansi().a(INTENSITY_BOLD)}" +
                    "ERROR: ${e.errorMessage}" +
                    "${Ansi.ansi().a(INTENSITY_BOLD_OFF)}${Ansi.ansi().fgDefault()}" +
                    "\n"
            )
            consolePrintln(e.usageHelp)

            Exiter.exit(ExitCode.RUNNER_FAILED)
        }
    }

    private fun consolePrintln(text: String) {
        ConsoleOutputCapturer.getOriginalTextWriter().print("$text\n")
    }

}

