package com.testerum.runner_cmdline.cmdline.params.exception

class CmdlineParamsParserException : RuntimeException {

    constructor(message: String) : super(message)

    constructor(message: String, cause: Throwable) : super(message, cause)

}
