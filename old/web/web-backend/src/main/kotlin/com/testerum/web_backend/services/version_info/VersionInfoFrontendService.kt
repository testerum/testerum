package com.testerum.web_backend.services.version_info

import com.testerum.common_jdk.asMap
import java.io.InputStream
import java.util.*

class VersionInfoFrontendService {

    companion object {
        private val VERSION_PROPERTIES_CLASSPATH_LOCATION = "version.properties"

        private val VERSION_PROPERTIES: Map<String, String> = run {
            val classLoader: ClassLoader = VersionInfoFrontendService::class.java.classLoader

            val inputStream: InputStream = classLoader.getResourceAsStream(VERSION_PROPERTIES_CLASSPATH_LOCATION)
                    ?: throw IllegalStateException("could not find classpath resource [$VERSION_PROPERTIES_CLASSPATH_LOCATION]")

            inputStream.use {
                val properties = Properties()

                properties.load(inputStream)

                TreeMap(properties.asMap())
            }
        }
    }

    fun getVersionProperties(): Map<String, String> = VERSION_PROPERTIES

    fun getProjectVersion(): String {
        return VERSION_PROPERTIES["projectVersion"]!!
    }

}
