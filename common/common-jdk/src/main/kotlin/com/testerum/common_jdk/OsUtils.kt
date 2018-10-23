package com.testerum.common_jdk

import java.nio.file.Path
import java.nio.file.Paths

object OsUtils {

    val OS_NAME: String = System.getProperty("os.name").toLowerCase()
    val IS_WINDOWS: Boolean = OS_NAME.startsWith("win")

    fun getJavaBinaryPath(): Path {
        val javaHome = Paths.get(
                System.getProperty("java.home")
        ).toAbsolutePath().normalize()

        return if (IS_WINDOWS) {
            javaHome.resolve("bin/java.exe")
        } else {
            javaHome.resolve("bin/java")
        }
    }

}