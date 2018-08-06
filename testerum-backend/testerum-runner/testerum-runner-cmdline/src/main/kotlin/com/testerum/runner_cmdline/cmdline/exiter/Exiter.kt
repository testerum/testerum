package com.testerum.runner_cmdline.cmdline.exiter

import com.testerum.runner_cmdline.cmdline.exiter.model.ExitCode
import kotlin.system.exitProcess

object Exiter {

    fun exit(exitCode: ExitCode): Nothing {
        System.err.println("exiting with code ${exitCode.name} (value ${exitCode.code})")

        exitProcess(exitCode.code)
    }

}
