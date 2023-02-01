package com.testerum.common_crypto.aes.encrypt_decrypt

import com.testerum.common_crypto.aes.ivgen.InitializationVectorGenerator
import com.testerum.common_crypto.common.CryptoProvider
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object AesDencrypter {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    const val ALGORITHM = "AES/GCM/NoPadding"
    private const val INITIALIZATION_VECTOR_SIZE_IN_BITS = 96 // recommended for GCM

    fun encrypt(dataToEncrypt: ByteArray,
                encryptionKey: SecretKey): EncryptionResult {
        val initializationVector = InitializationVectorGenerator.generateInitializationVector(INITIALIZATION_VECTOR_SIZE_IN_BITS)

        val cipher: Cipher = Cipher.getInstance(ALGORITHM, CryptoProvider.NAME)
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, IvParameterSpec(initializationVector))

        val encryptedData: ByteArray = cipher.doFinal(dataToEncrypt)

        return EncryptionResult(
                encryptedData = encryptedData,
                initializationVector = initializationVector
        )
    }

    fun decrypt(dataToDecrypt: ByteArray,
                decryptionKey: SecretKey,
                initializationVector: ByteArray): ByteArray {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM, CryptoProvider.NAME)
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey, IvParameterSpec(initializationVector))

        return cipher.doFinal(dataToDecrypt)
    }

}
