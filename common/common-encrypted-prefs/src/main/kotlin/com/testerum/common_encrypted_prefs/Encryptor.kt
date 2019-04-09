package com.testerum.common_encrypted_prefs

import java.util.Base64
import kotlin.experimental.xor

internal class Encryptor(private val secretKey: ByteArray) {

    companion object {
        private val BASE64_ENCODER: Base64.Encoder = Base64.getUrlEncoder()
        private val BASE64_DECODER: Base64.Decoder = Base64.getUrlDecoder()
    }

    //
    // Note
    // ----
    // The encryption in this class is predictable (given the same plaintext and secretKey,
    // you will always get the same result.
    //
    // This is by design. We need this property, because we encrypt lookup keys.
    //

    fun encrypt(plaintext: String): String {
        val plaintextBytes = plaintext.toByteArray(Charsets.UTF_8)

        val encryptedBytes = dencryptBytes(plaintextBytes)

        return BASE64_ENCODER.encodeToString(encryptedBytes)
    }

    fun decrypt(ciphertext: String): String {
        val ciphertextBytes: ByteArray = BASE64_DECODER.decode(ciphertext)

        val plaintextBytes = dencryptBytes(ciphertextBytes)

        return String(plaintextBytes, Charsets.UTF_8)
    }

    // this function works both to encrypt and decrypt data, because the algorithm is symmetric
    private fun dencryptBytes(plaintext: ByteArray): ByteArray {
        if (secretKey.isEmpty()) {
            throw IllegalArgumentException("cannot encrypt with an empty secret key")
        }

        val result = ByteArray(plaintext.size)
        var secretKeyIndex = 0

        for ((plainTextIndex, plaintextByte) in plaintext.withIndex()) {
            val secretKeyByte = secretKey[secretKeyIndex]
            secretKeyIndex++
            if (secretKeyIndex == secretKey.size) {
                secretKeyIndex = 0
            }

            result[plainTextIndex] = plaintextByte xor secretKeyByte
        }

        return result
    }

}
