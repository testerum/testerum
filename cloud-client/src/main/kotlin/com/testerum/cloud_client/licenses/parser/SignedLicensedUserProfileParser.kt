package com.testerum.cloud_client.licenses.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile
import com.testerum.common_crypto.rsa.sign.RsaSigner
import java.security.PublicKey
import java.util.Base64

class SignedLicensedUserProfileParser(private val objectMapper: ObjectMapper,
                                      private val publicKeyForSignatureVerification: PublicKey) {

    fun parse(signedLicensedUserProfile: String): LicensedUserProfile {
        val indexOfFirstNewline = signedLicensedUserProfile.indexOf('\n')
        if (indexOfFirstNewline == -1) {
            throw IllegalArgumentException("malformed signed data")
        }

        val signatureBase64 = signedLicensedUserProfile.substring(0, indexOfFirstNewline)
        val jsonUser = signedLicensedUserProfile.substring(indexOfFirstNewline + 1)

        val signature: ByteArray = Base64.getDecoder().decode(signatureBase64)
        val dataToVerify: ByteArray = jsonUser.toByteArray(Charsets.UTF_8)

        RsaSigner.verifySignature(dataToVerify, publicKeyForSignatureVerification, signature)

        return objectMapper.readValue(jsonUser)
    }

    fun isValidLicense(signedLicensedUserProfile: String): Boolean {
        return try {
            parse(signedLicensedUserProfile)

            true
        } catch (e: Exception) {
            false
        }
    }

}
