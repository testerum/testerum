package com.testerum.runner_cmdline.runner_tree.nodes.test

class RunnerTestException(message: String, cause: Throwable? = null, suppressedException: Throwable? = null) : RuntimeException(message, cause, true, false) {

    init {
        suppressedException?.let { addSuppressed(it) }
    }

}
