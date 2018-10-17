package com.testerum.common_crypto.rsa.encrypt_decrypt

import com.testerum.common_crypto.aes.keygen.AesKeyGenerator
import com.testerum.common_crypto.common.CryptoProvider
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

object RsaDencrypter {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    private const val ALGORITHM = "RSA/None/OAEPWithSHA-256AndMGF1Padding"

    fun encrypt(dataToEncrypt: ByteArray,
                encryptionKey: PublicKey): ByteArray {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM, CryptoProvider.NAME)
        cipher.init(Cipher.ENCRYPT_MODE, encryptionKey)

        return cipher.doFinal(dataToEncrypt)
    }

    fun encryptSecretKey(secretKeyToEncrypt: SecretKey,
                         encryptionKey: PublicKey): ByteArray {
        return encrypt(secretKeyToEncrypt.encoded, encryptionKey)
    }

    fun decrypt(dataToDecrypt: ByteArray,
                decryptionKey: PrivateKey): ByteArray {
        val cipher: Cipher = Cipher.getInstance(ALGORITHM, CryptoProvider.NAME)
        cipher.init(Cipher.DECRYPT_MODE, decryptionKey)

        return cipher.doFinal(dataToDecrypt)
    }

    fun decryptSecretKey(encryptedSecretKey: ByteArray,
                         decryptionKey: PrivateKey): SecretKey {
        val decryptedSecretKey = decrypt(encryptedSecretKey, decryptionKey)

        return SecretKeySpec(decryptedSecretKey, AesKeyGenerator.ALGORITHM)
    }

}
