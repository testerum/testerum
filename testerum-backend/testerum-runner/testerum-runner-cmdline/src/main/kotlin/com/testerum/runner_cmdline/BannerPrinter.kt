package com.testerum.runner_cmdline

object BannerPrinter {

    private val BANNER = """
        |  _____         _
        | |_   _|__  ___| |_ ___ _ __ _   _ _ __ ___
        |   | |/ _ \/ __| __/ _ \ '__| | | | '_ ` _ \
        |   | |  __/\__ \ ||  __/ |  | |_| | | | | | |
        |   |_|\___||___/\__\___|_|   \__,_|_| |_| |_|
        |""".trimMargin()

    fun printBanner() {
        println(BANNER)
    }

}