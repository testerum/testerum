package com.testerum.runner_cmdline.version

import com.testerum.common_jdk.asMap
import java.io.InputStream
import java.util.*

object RunnerVersionInfoService {

    private val VERSION_PROPERTIES_CLASSPATH_LOCATION = "version.properties"

    private val versionProperties: Map<String, String> = run {
        val contextClassLoader: ClassLoader = Thread.currentThread().contextClassLoader

        val inputStream: InputStream = contextClassLoader.getResourceAsStream(VERSION_PROPERTIES_CLASSPATH_LOCATION)
                ?: throw IllegalStateException("could not find classpath resource [$VERSION_PROPERTIES_CLASSPATH_LOCATION]")

        inputStream.use {
            val properties = Properties()

            properties.load(inputStream)

            return@run TreeMap(properties.asMap())
        }
    }

    fun getFormattedVersionProperties(): String {
        return """
            Testerum Runner ${versionProperties["projectVersion"]}

            Build timestamp : ${versionProperties["buildTimestamp"]}
            GIT revision    : ${versionProperties["gitRevision"]}
            GIT branch      : ${versionProperties["gitBranch"]}
            GIT tag         : ${versionProperties["gitTag"]}
        """.trimIndent()
    }
}
