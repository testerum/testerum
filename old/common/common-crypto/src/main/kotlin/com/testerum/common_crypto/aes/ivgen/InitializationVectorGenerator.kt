package com.testerum.common_crypto.aes.ivgen

import com.testerum.common_crypto.common.CryptoProvider
import java.security.SecureRandom

object InitializationVectorGenerator {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    private val SECURE_RANDOM = SecureRandom()

    fun generateInitializationVector(sizeInBits: Int): ByteArray {
        if (sizeInBits % 8 != 0) {
            throw IllegalArgumentException("sizeInBits must be a multiple of 8, but was $sizeInBits")
        }

        val initializationVector = ByteArray(sizeInBits / 8)

        SECURE_RANDOM.nextBytes(initializationVector)

        return initializationVector
    }

}
