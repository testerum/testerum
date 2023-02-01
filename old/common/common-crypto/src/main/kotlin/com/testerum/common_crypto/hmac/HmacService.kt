package com.testerum.common_crypto.hmac

import com.testerum.common_crypto.common.CryptoProvider
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacService {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    fun hmacSha256(data: ByteArray,
                   secretKey: ByteArray): ByteArray {
        val algorithm = "HmacSHA256"

        val hmac: Mac = Mac.getInstance(algorithm, CryptoProvider.NAME)
        hmac.init(
                SecretKeySpec(secretKey, algorithm)
        )

        return hmac.doFinal(data)
    }

}
