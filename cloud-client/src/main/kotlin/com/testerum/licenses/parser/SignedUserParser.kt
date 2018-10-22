package com.testerum.licenses.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_crypto.rsa.sign.RsaSigner
import com.testerum.licenses.model.user.User
import java.security.PublicKey
import java.util.*

class SignedUserParser(private val objectMapper: ObjectMapper,
                       private val publicKeyForSignatureVerification: PublicKey) {

    fun parse(signedUser: String): User {
        val indexOfFirstNewline = signedUser.indexOf('\n')
        if (indexOfFirstNewline == -1) {
            throw IllegalArgumentException("malformed signed data")
        }

        val signatureBase64 = signedUser.substring(0, indexOfFirstNewline)
        val jsonUser = signedUser.substring(indexOfFirstNewline + 1)

        val signature: ByteArray = Base64.getDecoder().decode(signatureBase64)
        val dataToVerify: ByteArray = jsonUser.toByteArray(Charsets.UTF_8)

        RsaSigner.verifySignature(dataToVerify, publicKeyForSignatureVerification, signature)

        return objectMapper.readValue(jsonUser)
    }

}
