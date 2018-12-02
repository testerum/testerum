package com.testerum.runner_cmdline

import com.testerum.common_cmdline.banner.TesterumBanner
import com.testerum.common_jdk.stopwatch.StopWatch
import com.testerum.runner.exit_code.ExitCode
import com.testerum.runner_cmdline.cmdline.exiter.Exiter
import com.testerum.runner_cmdline.cmdline.params.CmdlineParamsParser
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserVersionHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import com.testerum.runner_cmdline.module_di.RunnerModuleBootstrapper
import com.testerum.runner_cmdline.module_di.TesterumRunnerLoggingConfigurator
import com.testerum.runner_cmdline.version.RunnerVersionInfoService
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD
import org.fusesource.jansi.Ansi.Attribute.INTENSITY_BOLD_OFF
import org.fusesource.jansi.AnsiConsole

object TesterumRunner {

    @JvmStatic
    fun main(args: Array<String>) {
        val stopWatch = StopWatch.start()
        TesterumRunnerLoggingConfigurator.configureLogging()
        AnsiConsole.systemInstall()
        println(TesterumBanner.BANNER)

        val cmdlineParams: CmdlineParams = getCmdlineParams(args)
        println("cmdlineParams = $cmdlineParams")

        val bootstrapper = RunnerModuleBootstrapper(cmdlineParams, stopWatch)
        val exitCode: ExitCode = bootstrapper.context.use {
            val runnerApplication = bootstrapper.runnerModuleFactory.runnerApplication

            runnerApplication.execute(cmdlineParams)
        }

        Exiter.exit(exitCode)
    }

    private fun getCmdlineParams(args: Array<out String>): CmdlineParams {
        return try {
            CmdlineParamsParser.parse(*args)
        } catch (e: CmdlineParamsParserHelpRequestedException) {
            System.err.println(e.usageHelp)

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserVersionHelpRequestedException) {
            System.err.println(
                    RunnerVersionInfoService.getFormattedVersionProperties()
            )

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserParsingException) {
            System.err.println(
                    "${Ansi.ansi().fgBrightRed()}${Ansi.ansi().a(INTENSITY_BOLD)}" +
                    "ERROR: ${e.errorMessage}" +
                    "${Ansi.ansi().a(INTENSITY_BOLD_OFF)}${Ansi.ansi().fgDefault()}" +
                    "\n"
            )
            System.err.println(e.usageHelp)

            Exiter.exit(ExitCode.RUNNER_FAILED)
        }
    }

}

