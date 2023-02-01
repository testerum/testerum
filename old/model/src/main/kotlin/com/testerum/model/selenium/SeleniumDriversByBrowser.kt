package com.testerum.model.selenium

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.testerum.common_jdk.ComparableVersion
import com.testerum_api.testerum_steps_api.test_context.settings.model.SeleniumBrowserType
import java.util.TreeMap

class SeleniumDriversByBrowser(driversByBrowser: Map<SeleniumBrowserType, List<SeleniumDriverInfo>>) {

    @get:JsonAnyGetter
    val driversByBrowser: Map<SeleniumBrowserType, List<SeleniumDriverInfo>> = sortDriversByBrowserVersion(driversByBrowser)


    companion object {
        /**
         * For each browser, sorts the list of drivers to have the driver for the most recent version
         * towards the beginning of the list.
         */
        private fun sortDriversByBrowserVersion(driversByBrowser: Map<SeleniumBrowserType, List<SeleniumDriverInfo>>): Map<SeleniumBrowserType, List<SeleniumDriverInfo>> {
            val result = TreeMap<SeleniumBrowserType, List<SeleniumDriverInfo>>()

            for ((browserType, driverInfos) in driversByBrowser) {
                result[browserType] = driverInfos.sortedByDescending { driverInfo ->
                    driverInfo.browserVersions.maxByOrNull { version: String ->
                        ComparableVersion(version)
                    }
                }
            }

            return result
        }
    }

    @JsonIgnore
    fun getDriverInfoByBrowserAndDriverVersion(
        browserType: SeleniumBrowserType,
        driverVersion: String?
    ): SeleniumDriverInfo? {
        val drivers = driversByBrowser[browserType]
            ?: return null

        return if (driverVersion == null) {
            // find the driver with the most recent version
            drivers.maxByOrNull { ComparableVersion(it.driverVersion) }
        } else {
            drivers.find { it.driverVersion == driverVersion }
        }
    }

}
