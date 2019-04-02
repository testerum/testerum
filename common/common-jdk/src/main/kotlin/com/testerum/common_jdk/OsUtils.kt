package com.testerum.common_jdk

import java.nio.file.Path
import java.nio.file.Paths

object OsUtils {

    val OS_NAME: String = System.getProperty("os.name")
    val OS_ARCHITECTURE: String = System.getProperty("os.arch")

    val IS_WINDOWS: Boolean = OS_NAME.startsWith(prefix = "Windows", ignoreCase = true)
    val IS_MAC: Boolean = OS_NAME.startsWith(prefix = "Mac", ignoreCase = true)
    val IS_LINUX: Boolean = OS_NAME.startsWith(prefix = "Linux", ignoreCase = true)

    val IS_32BIT_ARCHITECTURE = (OS_ARCHITECTURE == "x86") || (OS_ARCHITECTURE == "i386")
    val IS_64BIT_ARCHITECTURE = (OS_ARCHITECTURE == "amd64") || (OS_ARCHITECTURE == "x86_64") || (OS_ARCHITECTURE == "i686")

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
