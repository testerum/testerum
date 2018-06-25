package com.testerum.runner.cmdline.exiter.model

enum class ExitCode(val code: Int) {

    OK                (0),
    INVALID_ARGS      (1),
    EXECUTION_FAILURE (2),

}
