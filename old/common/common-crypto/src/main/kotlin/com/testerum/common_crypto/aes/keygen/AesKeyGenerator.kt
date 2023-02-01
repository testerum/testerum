package com.testerum.common_crypto.aes.keygen

import com.testerum.common_crypto.common.CryptoProvider
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

object AesKeyGenerator {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    const val ALGORITHM = "AES"
    private const val KEY_SIZE_IN_BITS = 256

    fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(ALGORITHM, CryptoProvider.NAME)
        keyGenerator.init(KEY_SIZE_IN_BITS)

        return keyGenerator.generateKey()
    }

}
