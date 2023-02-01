package com.testerum.runner_cmdline.help

object RunnerHelpInfoService {

    fun getUsageHelp(): String {
        val classLoader = Thread.currentThread().contextClassLoader
        val inputStream = classLoader.getResourceAsStream("com/testerum/runner-cmdline/cmdline-usage.txt")
            ?: throw IllegalStateException("cannot find usage help resource")

        return inputStream.bufferedReader(charset = Charsets.UTF_8).use {
            it.readText()
        }
    }

}
