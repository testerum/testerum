package com.testerum_api.testerum_steps_api.test_context.settings.model

data class SeleniumDriverSettingValue(val browserType: SeleniumBrowserType,
                                      val browserExecutablePath: String?,
                                      val headless: Boolean,
                                      val driverVersion: String?,
                                      val remoteUrl: String?)
