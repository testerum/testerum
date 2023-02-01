package com.testerum.runner.exit_code

enum class ExitCode(val code: Int) {

    OK                (0),
    TEST_SUITE_FAILED (1),
    RUNNER_FAILED     (2),

}
