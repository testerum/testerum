package com.testerum.model.selenium

data class SeleniumDriverInfo(val driverVersion: String,
                              val browserVersions: List<String>,
                              val relativePath: String?)
