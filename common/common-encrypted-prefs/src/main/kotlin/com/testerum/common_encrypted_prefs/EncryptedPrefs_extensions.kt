package com.testerum.common_encrypted_prefs

import java.time.LocalDate

fun EncryptedPrefs.setLocalDate(key:String, value: LocalDate) {
    val stringValue = value.toString()

    setString(key, stringValue)
}

fun EncryptedPrefs.getLocalDate(key: String): LocalDate? {
    val stringValue = getString(key)
            ?: return null

    return LocalDate.parse(stringValue)
}
