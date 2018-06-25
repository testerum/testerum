package net.qutester.controller.config

import net.qutester.model.enums_ui.CompareModeForHttpResponseVerifyHeaders
import net.qutester.model.enums_ui.UiConfig
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/config")
open class ConfigController() {

    private val configsMap: Map<String, List<UiConfig>> = hashMapOf(
            "COMPARE_MODE_FOR_HTTP_RESPONSE_VERIFY_HEADERS" to CompareModeForHttpResponseVerifyHeaders.values().map { it as UiConfig}
    )

    @RequestMapping(path = arrayOf("/{configName:.+}"), method = arrayOf(RequestMethod.GET))
    @ResponseBody
    fun getResourcesPath(@PathVariable(value = "configName") configName: String): List<UiConfig> {
        return configsMap.get(configName)?: emptyList()
    }
}