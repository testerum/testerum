package com.testerum.common_crypto.string_obfuscator

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StringObfuscatorTest {

    @Test
    fun test() {
        val plaintext = "some super secret message"

        val encryptedText = StringObfuscator.obfuscate(plaintext)
        assertThat(encryptedText)
            .isNotEqualTo(plaintext)

        val decryptedText = StringObfuscator.deobfuscate(encryptedText)
        assertThat(decryptedText)
            .isEqualTo(plaintext)
    }

}
