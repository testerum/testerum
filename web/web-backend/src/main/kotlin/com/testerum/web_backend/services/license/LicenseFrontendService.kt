package com.testerum.web_backend.services.license

import com.testerum.common_crypto.password_hasher.PasswordHasher
import com.testerum.licenses.cache.LicensesCache
import com.testerum.licenses.cloud_client.CloudClient
import com.testerum.licenses.cloud_client.create_trial_account.CreateTrialAccountCloudRequest
import com.testerum.licenses.cloud_client.login.FoundLoginCloudResponse
import com.testerum.licenses.cloud_client.login.LoginCloudRequest
import com.testerum.licenses.cloud_client.login.NotFoundLoginCloudResponse
import com.testerum.licenses.model.user.User
import com.testerum.model.file.FileToUpload
import com.testerum.model.license.AuthRequest
import com.testerum.model.license.AuthResponse
import org.apache.commons.io.IOUtils

class LicenseFrontendService(private val cloudClient: CloudClient,
                             private val licensesCache: LicensesCache) {

    fun createTrialAccount(authRequest: AuthRequest): AuthResponse {
        val response = cloudClient.createTrialAccount(
                CreateTrialAccountCloudRequest(
                        email = authRequest.email,
                        password = authRequest.password
                )
        )

        val user = licensesCache.save(response.signedUser)

        return generateAuthResponse(user)
    }

    fun loginWithCredentials(authRequest: AuthRequest): AuthResponse? {
        // first attempt local login
        val userFromFile = licensesCache.getUserByEmail(authRequest.email)
        if (userFromFile != null) {
            if (PasswordHasher.isPasswordHashCorrect(authRequest.password, userFromFile.passwordHash)) {
                return generateAuthResponse(userFromFile)
            }
        }

        // if local login failed (user not present locally, or incorrect password), then attempt remote login
        val response = cloudClient.login(
                LoginCloudRequest(
                        email = authRequest.email,
                        password = authRequest.password
                )
        )

        return when (response) {
            is NotFoundLoginCloudResponse -> null
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