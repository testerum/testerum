package com.testerum.common_encrypted_prefs

import java.util.StringTokenizer
import java.util.prefs.Preferences

class EncryptedPrefs private constructor(private val secretKey: ByteArray,
                                         private val prefs: Preferences) {

    companion object {
        fun systemRoot(secretKey: ByteArray) = EncryptedPrefs(
                secretKey = secretKey,
                prefs = Preferences.systemRoot()
        )

        fun userRoot(secretKey: ByteArray) = EncryptedPrefs(
                secretKey = secretKey,
                prefs = Preferences.userRoot()
        )
    }

    private val encryptor = Encryptor(secretKey)

    fun node(path: String): EncryptedPrefs {
        val encryptedPath = encryptPath(path)

        return EncryptedPrefs(
                secretKey = secretKey,
                prefs = prefs.node(encryptedPath)
        )
    }

    fun setString(key: String, value: String) {
        val encryptedKey = encryptor.encrypt(key)
        val encryptedValue = encryptor.encrypt(value)

        prefs.put(encryptedKey, encryptedValue)
        prefs.flush()
    }

    fun getString(key: String): String? {
        val encryptedKey = encryptor.encrypt(key)

        val encryptedValue = prefs.get(encryptedKey, null)
                ?: return null

        return encryptor.decrypt(encryptedValue)
    }

    private fun encryptPath(path: String): String {
        val result = StringBuilder()

        val delimiter = "/"
        val tokenizer = StringTokenizer(path, delimiter, true)
        while (tokenizer.hasMoreTokens()) {
            val token: String = tokenizer.nextToken()

            if (token == delimiter) {
                result.append(token)
            } else {
                result.append(
                        encryptor.encrypt(token)
                )
            }
        }

        return result.toString()
    }

}
