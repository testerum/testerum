package com.testerum.web_backend.services.user

import com.testerum.cloud_client.CloudOfflineException
import com.testerum.cloud_client.licenses.CloudInvalidCredentialsException
import com.testerum.cloud_client.licenses.CloudNoValidLicenseException
import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.cloud_client.licenses.model.auth.CloudAuthRequest
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicenseStatus
import com.testerum.cloud_client.licenses.model.get_updated_licenses.GetUpdatedLicensesRequestItem
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.cloud_client.licenses.parser.SignedLicensedUserProfileParser
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
import java.time.temporal.ChronoUnit

class UserFrontendService(private val licenseCloudClient: LicenseCloudClient,
                          private val licensesCache: LicensesCache,
                          private val signedLicensedUserProfileParser: SignedLicensedUserProfileParser,
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
            val trialInfo = trialService.getTrialInfo()

            // daysUntilExpiration takes into consideration the current day,
            // but we want to show in the UI without the current day

            val trialInfoForUi = trialInfo.copy(
                    daysUntilExpiration = if (trialInfo.daysUntilExpiration == -1) {
                        -1
                    } else {
                        trialInfo.daysUntilExpiration - 1
                    }
            )

            LicenseInfo(
                    serverHasLicenses = false,
                    currentUserLicense = null,
                    trialLicense = trialInfoForUi
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
        val signedLicensedUserProfile: String = IOUtils.toString(
                licenseFile.inputStream,
                Charsets.UTF_8
        )

        if (!licensesCache.isLicenseValid(signedLicensedUserProfile)) {
            throw CloudInvalidCredentialsException("invalid license file")
        }

        try {
            if (!isLicenseValid(signedLicensedUserProfile)) {
                throw CloudNoValidLicenseException("this license file is invalid")
            }
        } catch (ignore: CloudOfflineException) {
            // If the cloud is offline, allow to use this license file.
            // This is the original purpose of license files: to be able to use Testerum without internet access.
        }

        val user = licensesCache.save(signedLicensedUserProfile)

        return generateAuthResponse(user)
    }

    private fun isLicenseValid(signedLicensedUserProfile: String): Boolean {
        val licensedUserProfile: LicensedUserProfile = try {
            signedLicensedUserProfileParser.parse(signedLicensedUserProfile)
        } catch (e: Exception) {
            // don't allow license that don't have the correct format
            // or the signature doesn't validate
            return false
        }

        val updatedLicenses = try {
            licenseCloudClient.getUpdatedLicenses(
                    listOf(
                            GetUpdatedLicensesRequestItem(
                                    email = licensedUserProfile.assigneeEmail,
                                    passwordHash = licensedUserProfile.passwordHash,
                                    existingLicenseId = licensedUserProfile.licenseId
                            )
                    )
            )
        } catch (e: Exception) {
            // if there was any error in contacting the cloud, consider the license valid
            return true
        }

        // only consider the license invalid if the could explicitly told us so (e.g. if the license was reassigned)
        return updatedLicenses[0].status != GetUpdatedLicenseStatus.NO_VALID_LICENSE
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
        val expired = nowUtc.isAfter(this.expirationDateUtc)

        val daysUntilExpiration = if (expired) {
            -1
        } else {
            // we subtract 1 because in the UI we want to show the number
            // without taking the current day into consideration
            ChronoUnit.DAYS.between(nowUtc, this.expirationDateUtc).toInt() - 1
        }

        return UserLicenseInfo(
                email = this.assigneeEmail,
                firstName = this.assigneeFirstName,
                lastName = this.assigneeLastName,
                creationDate = this.creationDateUtc,
                expirationDate = this.expirationDateUtc,
                daysUntilExpiration = daysUntilExpiration,
                expired = expired
        )
    }

}
