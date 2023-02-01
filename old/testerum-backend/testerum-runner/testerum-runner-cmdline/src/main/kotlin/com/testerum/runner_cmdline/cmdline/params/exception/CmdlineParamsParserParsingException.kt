package com.testerum.runner_cmdline.cmdline.params.exception

class CmdlineParamsParserParsingException(val errorMessage: String,
                                          val usageHelp: String) : RuntimeException() {

    override val message: String?
        get() = "ERROR: $errorMessage\n" +
               "\n" +
                usageHelp

}
