package com.testerum.web_backend.services.user

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
import org.apache.commons.io.IOUtils
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

class UserFrontendService(private val licenseCloudClient: LicenseCloudClient,
                          private val licensesCache: LicensesCache,
                          private val trialService: TrialService) {

    fun getLicenseInfo(): LicenseInfo {
        return if (licensesCache.hasAtLeastOneLicense()) {
            LicenseInfo(
                    serverHasLicenses = true,
                    currentUserLicense = null, // todo: implement this
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
        val token = licenseCloudClient.auth(
                CloudAuthRequest(
                        email = authRequest.email,
                        password = authRequest.password
                )
        )

        val signedLicense = licenseCloudClient.getSignedLicense(token)

        val licensedUserProfile = licensesCache.save(signedLicense)

        return generateAuthResponse(licensedUserProfile)
//        return AuthResponse()

//        return when (response) {
//            is NotFoundLoginCloudResponse -> throw CloudClientErrorResponseException(
//                    ErrorCloudResponse(
//                            CloudError(HttpStatus.SC_BAD_REQUEST, "User [${authRequest.email}] was not found or the password was incorrect.")
//                    )
//            )
//            is FoundLoginCloudResponse -> {
//                val savedUser = licensesCache.save(response.signedLicensedUserProfile)
//
//                generateAuthResponse(savedUser)
//            }
//        }
    }

    fun loginWithLicenseFile(licenseFile: FileToUpload): AuthResponse {
        val signedUser = IOUtils.toString(
                licenseFile.inputStream,
                Charsets.UTF_8
        )

        val user = licensesCache.save(signedUser)

        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(license: LicensedUserProfile): AuthResponse {
        val nowUtc = LocalDate.now(ZoneId.of("UTC"))
        val expired = nowUtc.isBefore(license.creationDateUtc)
                || nowUtc == license.expirationDateUtc
                || nowUtc.isAfter(license.expirationDateUtc)

        return AuthResponse(
                authToken = UUID.randomUUID().toString(), // todo: implement this
                currentUserLicense = UserLicenseInfo(
                        email = license.assigneeEmail,
                        firstName = license.assigneeFirstName,
                        lastName = license.assigneeLastName,
                        creationDate = license.creationDateUtc, // todo: convert to server timezone
                        expirationDate = license.expirationDateUtc, // todo: convert to server timezone
                        expired = expired
                )
        )
    }


}
