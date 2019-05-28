package com.testerum.cloud_client.licenses.cache.updater

import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import java.nio.file.Path as JavaPath

data class LicensesCacheEntry(val licenseFile: JavaPath,
                              val licenseFileContent: String,
                              val licensedUserProfile: LicensedUserProfile)
