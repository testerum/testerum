package com.testerum.cloud_client.licenses.file

import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.cloud_client.licenses.parser.SignedLicensedUserProfileParser
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.walkAndCollect
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path as JavaPath

class LicenseFileService(private val signedLicensedUserProfileParser: SignedLicensedUserProfileParser) {

    fun save(signedLicensedUserProfile: String,
             licensesDir: JavaPath): LicensedUserProfile {
        val licensedUserProfile = signedLicensedUserProfileParser.parse(signedLicensedUserProfile)

        val fileName = URLEncoder.encode(
                licensedUserProfile.assigneeEmail,
                Charsets.UTF_8.name()
        )

        val filePath = licensesDir.resolve(fileName)

        filePath.parent.createDirectories()

        Files.write(
                filePath,
                signedLicensedUserProfile.toByteArray(Charsets.UTF_8)
        )

        return licensedUserProfile
    }

    fun getLicenses(licensesDir: JavaPath): List<LicensedUserProfile> {
        val result = mutableListOf<LicensedUserProfile>()

        val licenseFiles: List<JavaPath> = licensesDir.walkAndCollect { true }
        for (licenseFile in licenseFiles) {
            try {
                val signedLicensedUserProfile = String(
                        Files.readAllBytes(licenseFile),
                        Charsets.UTF_8
                )

                result += signedLicensedUserProfileParser.parse(signedLicensedUserProfile)
            } catch (ignored: Exception) {
                // ignore invalid license files
            }
        }

        return result
    }

    fun isLicenseValid(signedLicensedUserProfile: String): Boolean {
        return signedLicensedUserProfileParser.isLicenseValid(signedLicensedUserProfile)
    }

}
