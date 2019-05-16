package com.testerum.cloud_client.licenses.cache

import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.validator.LicensesCacheEntry
import com.testerum.cloud_client.licenses.file.LicenseFileService
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
                    ?: throw IllegalStateException("cannot license because the licensesDir is not set")

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

    fun validate() {
        lock.write {
            val newLicensesByEmail = HashMap<String, LicensesCacheEntry>()

            for (cacheEntry in licensesByEmail.values) {
                if (cacheEntry.isValid()) {
                    newLicensesByEmail[cacheEntry.licensedUserProfile.assigneeEmail] = cacheEntry
                } else {
                    cacheEntry.licenseFile.deleteIfExists()
                }
            }

            this.licensesByEmail = newLicensesByEmail
        }
    }

    private fun LicensesCacheEntry.isValid(): Boolean {
        return try {
            licenseCloudClient.isLicenseValid(licenseFileContent)
        } catch (e: Exception) {
            // only invalidate the license if both:
            // (1) the cloud function responded correctly (200 OK)
            // (2) and the response is "false"
            true
        }
    }
}
