package com.testerum.cloud_client.licenses.cache

import com.testerum.cloud_client.licenses.file.LicenseFileService
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class LicensesCache(private val licenseFileService: LicenseFileService) {

    private val lock = ReentrantReadWriteLock()

    private var licensesDir: JavaPath? = null

    private var licensesByEmail: Map<String, LicensedUserProfile> = emptyMap()

    fun initialize(licensesDir: JavaPath) {
        lock.write {
            this.licensesDir = licensesDir

            val users = licenseFileService.getLicenses(licensesDir)

            this.licensesByEmail = users.associateBy { it.assigneeEmail }
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

    fun getLicenseByEmail(email: String): LicensedUserProfile? = lock.read { licensesByEmail[email] }

    fun hasAtLeastOneLicense(): Boolean = lock.read { licensesByEmail.isNotEmpty() }

    fun isLicenseValid(signedLicensedUserProfile: String): Boolean {
        return licenseFileService.isLicenseValid(signedLicensedUserProfile)
    }

}
