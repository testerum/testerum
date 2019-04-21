package com.testerum.web_backend.services.user.security

import com.testerum.common_crypto.hmac.HmacService
import com.testerum.web_backend.services.user.security.dao.TesterumUserDao
import com.testerum.web_backend.services.user.security.model.AuthenticatedAuthenticationResult
import com.testerum.web_backend.services.user.security.model.AuthenticationResult
import com.testerum.web_backend.services.user.security.model.UnauthenticatedAuthenticationResult
import java.util.Base64
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.absoluteValue

class AuthTokenService(private val testerumUserDao: TesterumUserDao) {

    companion object {
        private val BASE64_ENCODER = Base64.getUrlEncoder().withoutPadding()
        private val BASE64_DECODER = Base64.getUrlDecoder()
    }

    fun newAuthToken(email: String): String {
        // the token has the format: base64(hmac(random, passwordHash)) + "." + base64(email) + "." + random

        val testerumUser = testerumUserDao.getUserByEmail(email)
                ?: throw IllegalArgumentException("unknown user [$email]")

        val random = ThreadLocalRandom.current().nextLong().absoluteValue.toString(36)

        val base64Email = BASE64_ENCODER.encodeToString(
                email.toByteArray(charset = Charsets.UTF_8)
        )

        val hmac = HmacService.hmacSha256(
                data = random.toByteArray(charset = Charsets.UTF_8),
                secretKey = testerumUser.passwordHash.toByteArray(charset = Charsets.UTF_8)
        )

        val base64Hmac: String = BASE64_ENCODER.encodeToString(hmac)

        @Suppress("ConvertToStringTemplate")
        return base64Hmac + "." + base64Email+ "." + random
    }

    /**
     * @return - the email associated with this token, if the token is valid
     *  - null, if the token is not valid
     */
    fun authenticate(token: String): AuthenticationResult {
        val indexOffFirstDot = token.indexOf('.')
        if (indexOffFirstDot == -1) {
            return UnauthenticatedAuthenticationResult
        }

        val indexOfSecondDot = token.indexOf('.', indexOffFirstDot + 1)
        if (indexOfSecondDot == -1) {
            return UnauthenticatedAuthenticationResult
        }

        val base64Hmac = token.substring(0, indexOffFirstDot)
        val base64Email = token.substring(indexOffFirstDot + 1, indexOfSecondDot)
        val random = token.substring(indexOfSecondDot + 1)

        val email = String(
                BASE64_DECODER.decode(base64Email),
                Charsets.UTF_8
        )
        val testerumUser = testerumUserDao.getUserByEmail(email)
                ?: return UnauthenticatedAuthenticationResult

        val actualHmac: ByteArray = BASE64_DECODER.decode(base64Hmac)

        val hmac = HmacService.hmacSha256(
                data = random.toByteArray(charset = Charsets.UTF_8),
                secretKey = testerumUser.passwordHash.toByteArray(charset = Charsets.UTF_8)
        )

        return if (actualHmac.contentEquals(hmac)) {
            AuthenticatedAuthenticationResult(
                    user = testerumUser
            )
        } else {
            UnauthenticatedAuthenticationResult
        }
    }
}
