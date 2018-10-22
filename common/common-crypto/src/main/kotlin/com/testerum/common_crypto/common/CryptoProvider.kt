package com.testerum.common_crypto.common

import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

object CryptoProvider {

    // todo: unit tests

    const val NAME = "BC"

    fun ensureProviderRegistered() {
        Security.insertProviderAt(
                BouncyCastleProvider(),
                1
        )
    }

}
