package com.testerum.api.test_context.settings.model

data class SeleniumDriverSettingValue(val browserType: SeleniumBrowserType,
                                      val driverExecutablePath: String?,
                                      val headless: Boolean,
                                      val driverVersion: String)
