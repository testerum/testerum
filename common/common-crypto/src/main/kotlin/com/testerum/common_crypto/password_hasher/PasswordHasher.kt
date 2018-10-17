package com.testerum.common_crypto.password_hasher

import com.testerum.common_crypto.common.CryptoProvider
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    private const val KDF_ALGORITHM = "PBKDF2WithHmacSHA512"

    private const val keyLengthInBits = 512
    private const val keySizeInBytes = keyLengthInBits / 8
    private const val saltSizeInBytes = keySizeInBytes

    private val random = SecureRandom()
    private val base64Encoder = Base64.getEncoder()

    fun generatePasswordHash(password: String,
                             iterations: Int): String {
        return hashPassword(
                password = password,
                salt = generateSalt(),
                iterations = iterations
        )
    }

    fun isPasswordHashCorrect(password: String,
                              expectedHash: String): Boolean {
        val parts: List<String> = expectedHash.split('.')
        if (parts.size != 3) {
            throw IllegalArgumentException("malformed expectedHash")
        }

        val salt: ByteArray = Base64.getDecoder().decode(parts[0])
        val iterations = parts[1].toInt()

        val computedHash = hashPassword(password, salt, iterations)

        return computedHash == expectedHash
    }

    private fun hashPassword(password: String,
                             salt: ByteArray,
                             iterations: Int): String {
        val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(KDF_ALGORITHM, CryptoProvider.NAME)
        val keySpec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLengthInBits)
        val secretKey: SecretKey = secretKeyFactory.generateSecret(keySpec)

        val saltBase64 = base64Encoder.encodeToString(salt)
        val keyBase64 = base64Encoder.encodeToString(secretKey.encoded)

        return "$saltBase64.$iterations.$keyBase64"
    }

    private fun generateSalt(): ByteArray {
        val salt = ByteArray(saltSizeInBytes)

        random.nextBytes(salt)

        return salt
    }

}