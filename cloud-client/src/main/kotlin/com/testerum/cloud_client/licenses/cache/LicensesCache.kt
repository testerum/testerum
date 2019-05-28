package com.testerum.cloud_client.licenses.cache

import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.updater.LicensesCacheEntry
import com.testerum.cloud_client.licenses.file.LicenseFileService
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicenseStatus
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicensesRequestItem
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.common_kotlin.deleteIfExists
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class LicensesCache(private val licenseFileService: LicenseFileService,
                    private val licenseCloudClient: LicenseCloudClient) {

    private val lock = ReentrantReadWriteLock()

    private var licensesDir: JavaPath? = null

    private var licensesByEmail: Map<String, LicensesCacheEntry> = emptyMap()

    fun initialize(licensesDir: JavaPath) {
        lock.write {
            this.licensesDir = licensesDir

            val users = licenseFileService.getLicenses(licensesDir)

            this.licensesByEmail = users.associateBy { it.licensedUserProfile.assigneeEmail }
        }
    }

    fun save(signedLicensedUserProfile: String): LicensedUserProfile {
        lock.write {
            val licensesDir = this.licensesDir
                    ?: throw IllegalStateException("cannot save license because the licensesDir is not set")

            val license = licenseFileService.save(signedLicensedUserProfile, licensesDir)

            initialize(licensesDir)

            return license
        }
    }

    fun getLicenseByEmail(email: String): LicensedUserProfile? = lock.read { licensesByEmail[email]?.licensedUserProfile }

    fun hasAtLeastOneLicense(): Boolean = lock.read { licensesByEmail.isNotEmpty() }

    fun isLicenseValid(signedLicensedUserProfile: String): Boolean {
        return licenseFileService.isLicenseValid(signedLicensedUserProfile)
    }

    fun updateFromCloud() {
        lock.write {
            val licensesDir = this.licensesDir
                    ?: throw IllegalStateException("cannot update local license cache because the licensesDir is not set")

            val requestItems = licensesByEmail.values.map {
                GetUpdatedLicensesRequestItem(
                        email = it.licensedUserProfile.assigneeEmail,
                        passwordHash = it.licensedUserProfile.passwordHash,
                        existingLicenseId = it.licensedUserProfile.licenseId
                )
            }

            val responseItems = licenseCloudClient.getUpdatedLicenses(requestItems)

            for ((i, existingCacheEntry) in licensesByEmail.values.withIndex()) {
                val responseItem = responseItems[i]

                if (responseItem.status == GetUpdatedLicenseStatus.NO_VALID_LICENSE) {
                    // delete outdated license
                    existingCacheEntry.licenseFile.deleteIfExists()
                } else if (responseItem.status == GetUpdatedLicenseStatus.UPDATED) {
                    // update existing license
                    existingCacheEntry.licenseFile.deleteIfExists()

                    licenseFileService.save(responseItem.updatedSignedLicensedUserProfile!!, licensesDir)
                }
            }

            initialize(licensesDir)
        }
    }

}
