package com.testerum.common_crypto.string_obfuscator

import java.util.*
import kotlin.experimental.xor

object StringObfuscator {

    private val base64Encoder: Base64.Encoder = Base64.getEncoder()
    private val base64Decoder: Base64.Decoder = Base64.getDecoder()
    private val random = Random() // not using SecureRandom, because this class does obfuscation, not encryption (so we don't need the security)

    private val secretKey: ByteArray = base64Decoder.decode("5nXMMcwJ8wgs6JRTX/EN1A==")


    fun obfuscate(text: String): String {
        val dataToObfuscate = text.toByteArray(Charsets.UTF_8)

        val randomKey = generateRandomKey()
        val obfuscatedData = encryptDecrypt(dataToObfuscate, randomKey)

        val randomKeyBase64 = base64Encoder.encodeToString(randomKey)
        val obfuscatedDataBase64 = base64Encoder.encodeToString(obfuscatedData)

        @Suppress("ConvertToStringTemplate")
        return randomKeyBase64 + "." + obfuscatedDataBase64
    }

    fun deobfuscate(obfuscatedText: String): String {
        val indexOfDot = obfuscatedText.indexOf('.')
        if (indexOfDot == -1) {
            throw IllegalArgumentException("obfuscatedText is not obfuscated")
        }

        val randomKeyBase64 = obfuscatedText.substring(0, indexOfDot)
        val obfuscatedDataBase64 = obfuscatedText.substring(indexOfDot + 1)

        val randomKey = base64Decoder.decode(randomKeyBase64)
        val obfuscatedData = base64Decoder.decode(obfuscatedDataBase64)

        val deobfuscatedData = encryptDecrypt(obfuscatedData, randomKey)

        return String(deobfuscatedData, Charsets.UTF_8)
    }

    // Using a random key to prevent predictable obfuscation: obfuscating the same data multiple times
    // giving the same result.
    // Without this, one could search for an obfuscated string and find all the places where the same
    // un-obfuscated string appears - even if he doesn't know how to de-obfuscate.
    private fun encryptDecrypt(data: ByteArray,
                               randomKey: ByteArray): ByteArray {
        val result = ByteArray(data.size)

        for (i in 0 until data.size) {
            val dataByte = data[i]
            val secretKeyByte = secretKey[i % secretKey.size]
            val randomKeyByte = randomKey[i % randomKey.size]

            result[i] = dataByte xor secretKeyByte xor randomKeyByte
        }

        return result
    }

    private fun generateRandomKey(): ByteArray {
        val randomKey = ByteArray(16)

        random.nextBytes(randomKey)

        return randomKey
    }

}
