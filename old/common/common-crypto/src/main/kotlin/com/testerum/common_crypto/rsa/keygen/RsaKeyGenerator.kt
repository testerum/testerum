package com.testerum.common_crypto.rsa.keygen

import com.testerum.common_crypto.common.CryptoProvider
import java.security.KeyPair
import java.security.KeyPairGenerator

object RsaKeyGenerator {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    private const val ALGORITHM = "RSA"

    fun generateKeyPair(sizeInBits: Int): KeyPair {
        if (sizeInBits % 8 != 0) {
            throw IllegalArgumentException("sizeInBits must be a multiple of 8, but was $sizeInBits")
        }

        val keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM, CryptoProvider.NAME)
        keyPairGenerator.initialize(sizeInBits)

        return keyPairGenerator.generateKeyPair()
    }

}
