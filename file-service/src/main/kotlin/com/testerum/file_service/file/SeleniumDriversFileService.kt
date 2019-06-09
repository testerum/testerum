package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.api.test_context.settings.model.SeleniumBrowserType
import com.testerum.common_jdk.ComparableVersion
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.list
import com.testerum.model.selenium.FileSeleniumDriverInfo
import com.testerum.model.selenium.SeleniumDriverInfo
import com.testerum.model.selenium.SeleniumDriversByBrowser
import org.slf4j.LoggerFactory
import java.util.TreeMap
import java.nio.file.Path as JavaPath

class SeleniumDriversFileService {

    companion object {
        private val LOG = LoggerFactory.getLogger(SeleniumDriversFileService::class.java)

        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun getDriversInfo(seleniumDriversDir: JavaPath): SeleniumDriversByBrowser {
        val driversByBrowser = HashMap<SeleniumBrowserType, ArrayList<SeleniumDriverInfo>>()

        val browserPaths = seleniumDriversDir.list()

        for (browserPath in browserPaths) {
            val browserTypeCode = browserPath.fileName?.toString()?.toUpperCase() ?: ""
            val browserType = SeleniumBrowserType.safeValueOf(browserTypeCode)

            if (browserType == null) {
                LOG.warn("skipping unknown browser [${browserPath.toAbsolutePath().normalize()}]")
                continue
            }

            val newDescriptors = parseDriverDescriptorsForBrowser(seleniumDriversDir, browserPath)

            driversByBrowser.compute(browserType) {_, descriptors ->
                if (descriptors == null) {
                    ArrayList(newDescriptors)
                } else {
                    descriptors.addAll(newDescriptors)

                    descriptors
                }
            }
        }

        val sortedDriversByBrowser = sortDriversByBrowserMap(driversByBrowser)

        return SeleniumDriversByBrowser(driversByBrowser = sortedDriversByBrowser)
    }

    private fun parseDriverDescriptorsForBrowser(seleniumDriversDir: JavaPath,
                                                 browserPath: JavaPath): List<SeleniumDriverInfo> {
        val result = ArrayList<SeleniumDriverInfo>()

        val driverDescriptorFiles = browserPath.list { it.hasExtension(".json") }

        for (driverDescriptorFile in driverDescriptorFiles) {
            val fileSeleniumDriverInfo = parseDriverDescriptorFile(driverDescriptorFile)
            if (fileSeleniumDriverInfo == null) {
                LOG.warn("skipping invalid Selenium descriptor file at [${browserPath.toAbsolutePath().normalize()}]")
                continue
            }

            val relativePath: JavaPath = seleniumDriversDir.toAbsolutePath().normalize().relativize(browserPath.toAbsolutePath().normalize())

            val seleniumDriverInfo = SeleniumDriverInfo(
                    driverVersion = fileSeleniumDriverInfo.driverVersion,
                    browserVersions = fileSeleniumDriverInfo.browserVersions,
                    relativePath = relativePath.toString()
            )

            result += seleniumDriverInfo
        }

        return result
    }

    private fun parseDriverDescriptorFile(driverDescriptorFile: JavaPath): FileSeleniumDriverInfo? {
        return try {
            OBJECT_MAPPER.readValue(
                    driverDescriptorFile.toFile()
            )
        } catch (e: Exception) {
            LOG.error("failed to parse Selenium driver descriptor file at [${driverDescriptorFile.toAbsolutePath().normalize()}]", e)
            null
        }
    }

    private fun sortDriversByBrowserMap(driversByBrowser: Map<SeleniumBrowserType, ArrayList<SeleniumDriverInfo>>): Map<SeleniumBrowserType, List<SeleniumDriverInfo>> {
        val result = TreeMap<SeleniumBrowserType, List<SeleniumDriverInfo>>()

        for ((browserType, driverInfos) in driversByBrowser) {
            result[browserType] = driverInfos.sortedByDescending { driverInfo ->
                driverInfo.browserVersions.maxBy { version ->
                    ComparableVersion(version)
                }
            }
        }

        return result
    }
}
