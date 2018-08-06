package com.testerum.runner_cmdline

import com.testerum.runner_cmdline.cmdline.exiter.Exiter
import com.testerum.runner_cmdline.cmdline.exiter.model.ExitCode
import com.testerum.runner_cmdline.cmdline.params.CmdlineParamsParser
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserHelpRequestedException
import com.testerum.runner_cmdline.cmdline.params.exception.CmdlineParamsParserParsingException
import com.testerum.runner_cmdline.cmdline.params.model.CmdlineParams
import org.fusesource.jansi.Ansi
import org.springframework.context.support.ClassPathXmlApplicationContext

object TesterumRunner {

    @JvmStatic
    fun main(args: Array<String>) {
        val cmdlineParams: CmdlineParams = getCmdlineParams(args)

        SettingsManagerFactory.cmdlineParams = cmdlineParams
        
        val exitCode: ExitCode = ClassPathXmlApplicationContext("spring/spring_runner.xml").use { context ->
            val runner = context.getBean(RunnerApplication::class.java)

            runner.execute(cmdlineParams)
        }

        Exiter.exit(exitCode)
    }

    private fun getCmdlineParams(args: Array<out String>): CmdlineParams {
        return try {
            CmdlineParamsParser.parse(*args)
        } catch (e: CmdlineParamsParserHelpRequestedException) {
            System.err.println(e.usageHelp)

            Exiter.exit(ExitCode.OK)
        } catch (e: CmdlineParamsParserParsingException) {
            System.err.println(
                    "${Ansi.ansi().fgBrightRed()}${Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD)}" +
                            "ERROR: ${e.errorMessage}" +
                            "${Ansi.ansi().a(Ansi.Attribute.INTENSITY_BOLD_OFF)}${Ansi.ansi().fgDefault()}" +
                            "\n"
            )
            System.err.println(e.usageHelp)

            Exiter.exit(ExitCode.INVALID_ARGS)
        }
    }

}

