package com.testerum.model.selenium

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.testerum.api.test_context.settings.model.SeleniumBrowserType

data class SeleniumDriversByBrowser(@get:JsonAnyGetter val driversByBrowser: Map<SeleniumBrowserType, List<SeleniumDriverInfo>>)
