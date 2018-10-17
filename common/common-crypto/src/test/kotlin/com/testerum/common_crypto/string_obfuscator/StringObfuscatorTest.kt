package com.testerum.common_crypto.string_obfuscator

import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Test

class StringObfuscatorTest {

    @Test
    fun test() {
        val plaintext = "some super secret message"

        val encryptedText = StringObfuscator.obfuscate(plaintext)
        assertThat(encryptedText, not(equalTo(plaintext)))

        val decryptedText = StringObfuscator.deobfuscate(encryptedText)
        assertThat(decryptedText, equalTo(plaintext))
    }

}