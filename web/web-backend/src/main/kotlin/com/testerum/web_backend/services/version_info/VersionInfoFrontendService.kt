package com.testerum.web_backend.services.version_info

import com.testerum.common_jdk.asMap
import java.io.InputStream
import java.util.*

class VersionInfoFrontendService {

    companion object {
        private val VERSION_PROPERTIES_CLASSPATH_LOCATION = "version.properties"
    }

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

    fun getVersionProperties(): Map<String, String> = versionProperties

}
