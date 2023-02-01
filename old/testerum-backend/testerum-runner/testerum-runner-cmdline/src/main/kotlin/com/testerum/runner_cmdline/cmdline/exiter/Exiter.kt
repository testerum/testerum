package com.testerum.runner_cmdline.cmdline.exiter

import com.testerum.runner.exit_code.ExitCode
import kotlin.system.exitProcess

object Exiter {

    fun exit(exitCode: ExitCode): Nothing {
        exitProcess(exitCode.code)
    }

}
