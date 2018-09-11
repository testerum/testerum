package com.testerum.runner_cmdline

import org.intellij.lang.annotations.Language

object BannerPrinter {

    @Language("TEXT")
    private val BANNER = """
        |     __  __                                             __  __
        |    / / / / _____         _                             \ \ \ \
        |   / / / / |_   _|__  ___| |_ ___ _ __ _   _ _ __ ___    \ \ \ \
        |  ( ( ( (    | |/ _ \/ __| __/ _ \ '__| | | | '_ ` _ \    ) ) ) )
        |   \ \ \ \   | |  __/\__ \ ||  __/ |  | |_| | | | | | |  / / / /
        |    \_\ \_\  |_|\___||___/\__\___|_|   \__,_|_| |_| |_| /_/ /_/
        |""".trimMargin()

    fun printBanner() {
        println(BANNER)
    }

}
