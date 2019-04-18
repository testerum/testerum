package com.testerum.web_backend.services.user

import com.testerum.common_crypto.hmac.HmacService
import java.util.Base64
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

class AuthTokenService {

    companion object {
        private val BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding()
        private val BASE64_DECODER = Base64.getUrlDecoder()
    }

    fun newAuthToken(email: String, passwordHash: String): String {
        // the token has the format: base64(hmac(random, passwordHash)) + "." + base64(email) + "." + random

        val random = ThreadLocalRandom.current().nextLong().absoluteValue.toString(36)

        val base64Email = BASE64_ENCODER.encodeToString(
                email.toByteArray(charset = Charsets.UTF_8)
        )

        val hmac = HmacService.hmacSha256(
                data = random.toByteArray(charset = Charsets.UTF_8),
                secretKey = passwordHash.toByteArray(charset = Charsets.UTF_8)
        )

        val base64Hmac: String = BASE64_ENCODER.encodeToString(hmac)

        @Suppress("ConvertToStringTemplate")
        return base64Hmac + "." + base64Email+ "." + random
    }

    /**
     * @return - the email associated with this token, if the token is valid
     *  - null, if the token is not valid
     */
    fun getAuthenticatedEmail(token: String, getPasswordHashByEmail: (email: String) -> String?): String? {
        val indexOffFirstDot = token.indexOf('.')
        if (indexOffFirstDot == -1) {
            return null
        }

        val indexOfSecondDot = token.indexOf('.', indexOffFirstDot + 1)
        if (indexOfSecondDot == -1) {
            return null
        }

        val base64Hmac = token.substring(0, indexOffFirstDot)
        val base64Email = token.substring(indexOffFirstDot + 1, indexOfSecondDot)
        val random = token.substring(indexOfSecondDot + 1)

        val email = String(
                BASE64_DECODER.decode(base64Email),
                Charsets.UTF_8
        )
        val passwordHash = getPasswordHashByEmail(email)
                ?: return null

        val actualHmac: ByteArray = BASE64_DECODER.decode(base64Hmac)

        val hmac = HmacService.hmacSha256(
                data = random.toByteArray(charset = Charsets.UTF_8),
                secretKey = passwordHash.toByteArray(charset = Charsets.UTF_8)
        )

        return if (actualHmac.contentEquals(hmac)) {
            email
        } else {
            null
        }
    }
}
