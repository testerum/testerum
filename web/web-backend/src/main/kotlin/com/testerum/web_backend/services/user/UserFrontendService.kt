package com.testerum.web_backend.services.user

import com.testerum.cloud_client.CloudOfflineException
import com.testerum.cloud_client.licenses.CloudInvalidCredentialsException
import com.testerum.cloud_client.licenses.CloudNoValidLicenseException
import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.cloud_client.licenses.model.auth.CloudAuthRequest
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.common_crypto.password_hasher.PasswordHasher
import com.testerum.file_service.business.trial.TrialService
import com.testerum.model.file.FileToUpload
import com.testerum.model.user.auth.AuthRequest
import com.testerum.model.user.auth.AuthResponse
import com.testerum.model.user.license.LicenseInfo
import com.testerum.model.user.license.UserLicenseInfo
import com.testerum.web_backend.filter.security.CurrentUserHolder
import com.testerum.web_backend.services.user.security.AuthTokenService
import org.apache.commons.io.IOUtils
import java.time.LocalDate
import java.time.ZoneId

class UserFrontendService(private val licenseCloudClient: LicenseCloudClient,
                          private val licensesCache: LicensesCache,
                          private val trialService: TrialService,
                          private val authTokenService: AuthTokenService) {

    fun getLicenseInfo(): LicenseInfo {
        return if (licensesCache.hasAtLeastOneLicense()) {
            val currentUser = CurrentUserHolder.get()

            LicenseInfo(
                    serverHasLicenses = true,
                    currentUserLicense = currentUser?.toUserLicenceInfo(),
                    trialLicense = null
            )
        } else {
            LicenseInfo(
                    serverHasLicenses = false,
                    currentUserLicense = null,
                    trialLicense = trialService.getTrialInfo()
            )
        }
    }

    fun loginWithCredentials(authRequest: AuthRequest): AuthResponse {
        // first attempt local login
        val userFromFile = licensesCache.getLicenseByEmail(authRequest.email)
        if (userFromFile != null) {
            if (PasswordHasher.isPasswordHashCorrect(authRequest.password, userFromFile.passwordHash)) {
                return generateAuthResponse(userFromFile)
            }
        }

        // if local login failed (user not present locally, or incorrect password), then attempt remote login
        val cloudAuthRequest = CloudAuthRequest(
                email = authRequest.email,
                password = authRequest.password
        )
        val token = licenseCloudClient.auth(cloudAuthRequest)
                ?: throw CloudInvalidCredentialsException("invalid credentials for email=[${authRequest.email}]")

        val signedLicense = licenseCloudClient.getSignedLicense(token)

        if (!licensesCache.isLicenseValid(signedLicense)) {
            throw CloudInvalidCredentialsException("got invalid license from the cloud")
        }

        val licensedUserProfile = licensesCache.save(signedLicense)

        return generateAuthResponse(licensedUserProfile)
    }

    fun loginWithLicenseFile(licenseFile: FileToUpload): AuthResponse {
        val signedLicensedUserProfile = IOUtils.toString(
                licenseFile.inputStream,
                Charsets.UTF_8
        )

        if (!licensesCache.isLicenseValid(signedLicensedUserProfile)) {
            throw CloudInvalidCredentialsException("invalid license file")
        }

        try {
            if (!licenseCloudClient.isLicenseValid(signedLicensedUserProfile)) {
                throw CloudNoValidLicenseException("this license file is invalid")
            }
        } catch (ignore: CloudOfflineException) {
            // If the cloud is offline, allow to use this license file.
            // This is the original purpose of license files: to be able to use Testerum without internet access.
        }

        val user = licensesCache.save(signedLicensedUserProfile)

        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(licensedUserProfile: LicensedUserProfile): AuthResponse {
        val authToken = authTokenService.newAuthToken(
                email = licensedUserProfile.assigneeEmail
        )

        return AuthResponse(
                authToken = authToken,
                currentUserLicense = licensedUserProfile.toUserLicenceInfo()
        )
    }

    private fun LicensedUserProfile.toUserLicenceInfo(): UserLicenseInfo {
        val nowUtc = LocalDate.now(ZoneId.of("UTC"))
        val expired = nowUtc.isBefore(this.creationDateUtc)
                || nowUtc.isAfter(this.expirationDateUtc)

        return UserLicenseInfo(
                email = this.assigneeEmail,
                firstName = this.assigneeFirstName,
                lastName = this.assigneeLastName,
                creationDate = this.creationDateUtc,
                expirationDate = this.expirationDateUtc,
                expired = expired
        )
    }

}
