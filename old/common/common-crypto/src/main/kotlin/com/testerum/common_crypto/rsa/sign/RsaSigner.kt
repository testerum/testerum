package com.testerum.common_crypto.rsa.sign

import com.testerum.common_crypto.common.CryptoProvider
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature

object RsaSigner {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    private const val ALGORITHM = "SHA512withRSA"

    fun sign(dataToSign: ByteArray,
             privateKey: PrivateKey): ByteArray {
        val signature: Signature = Signature.getInstance(ALGORITHM, CryptoProvider.NAME)
        signature.initSign(privateKey)

        signature.update(dataToSign)

        return signature.sign()
    }

    /**
     * @throws InvalidSignatureException if the signature is not correct
     */
    fun verifySignature(dataToCheckSignatureFor: ByteArray,
                        publicKey: PublicKey,
                        signatureBytes: ByteArray) {
        if (!isSignatureOk(dataToCheckSignatureFor, publicKey, signatureBytes)) {
            throw InvalidSignatureException()
        }
    }

    fun isSignatureOk(dataToCheckSignatureFor: ByteArray,
                      publicKey: PublicKey,
                      signatureBytes: ByteArray): Boolean {
        val signature: Signature = Signature.getInstance(ALGORITHM, CryptoProvider.NAME)
        signature.initVerify(publicKey)

        signature.update(dataToCheckSignatureFor)

        return signature.verify(signatureBytes)
    }

}
