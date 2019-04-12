package com.testerum.web_backend.services.license

import com.testerum.cloud_client.infrastructure.CloudClientErrorResponseException
import com.testerum.cloud_client.infrastructure.CloudError
import com.testerum.cloud_client.infrastructure.ErrorCloudResponse
import com.testerum.cloud_client.licenses.LicenseCloudClient
import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.cloud_client.licenses.login.FoundLoginCloudResponse
import com.testerum.cloud_client.licenses.login.LoginCloudRequest
import com.testerum.cloud_client.licenses.login.NotFoundLoginCloudResponse
import com.testerum.cloud_client.licenses.model.user.User
import com.testerum.common_crypto.password_hasher.PasswordHasher
import com.testerum.model.file.FileToUpload
import com.testerum.model.license.auth.AuthRequest
import com.testerum.model.license.auth.AuthResponse
import org.apache.commons.io.IOUtils
import org.apache.http.HttpStatus

class LicenseFrontendService(private val licenseCloudClient: LicenseCloudClient,
                             private val licensesCache: LicensesCache) {

    fun loginWithCredentials(authRequest: AuthRequest): AuthResponse {
        // first attempt local login
        val userFromFile = licensesCache.getUserByEmail(authRequest.email)
        if (userFromFile != null) {
            if (PasswordHasher.isPasswordHashCorrect(authRequest.password, userFromFile.passwordHash)) {
                return generateAuthResponse(userFromFile)
            }
        }

        // if local login failed (user not present locally, or incorrect password), then attempt remote login
        val response = licenseCloudClient.login(
                LoginCloudRequest(
                        email = authRequest.email,
                        password = authRequest.password
                )
        )

        return when (response) {
            is NotFoundLoginCloudResponse -> throw CloudClientErrorResponseException(
                    ErrorCloudResponse(
                            CloudError(HttpStatus.SC_BAD_REQUEST, "User [${authRequest.email}] was not found or the password was incorrect.")
                    )
            )
            is FoundLoginCloudResponse -> {
                val savedUser = licensesCache.save(response.signedUser)

                generateAuthResponse(savedUser)
            }
        }
    }

    fun loginWithLicenseFile(licenseFile: FileToUpload): AuthResponse {
        val signedUser = IOUtils.toString(
                licenseFile.inputStream,
                Charsets.UTF_8
        )

        val user = licensesCache.save(signedUser)

        return generateAuthResponse(user)
    }

    private fun generateAuthResponse(user: User): AuthResponse {
        // todo: what to return for authentication token?
        return AuthResponse(
                email = user.email,
                name = user.name,
                companyName = user.companyName,
                licenseExpirationDate = user.assignedLicense.expirationDateUtc,
                authToken = "blah"
        )
    }

}
