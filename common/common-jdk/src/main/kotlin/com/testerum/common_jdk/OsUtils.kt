package com.testerum.common_jdk

object OsUtils {

    val OS_NAME: String = System.getProperty("os.name").toLowerCase()
    val IS_WINDOWS: Boolean = OS_NAME.startsWith("win")

}