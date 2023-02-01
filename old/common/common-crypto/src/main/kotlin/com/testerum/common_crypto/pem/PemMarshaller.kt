package com.testerum.common_crypto.pem

import com.testerum.common_crypto.common.CryptoProvider
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.openssl.PEMDecryptorProvider
import org.bouncycastle.openssl.PEMEncryptedKeyPair
import org.bouncycastle.openssl.PEMKeyPair
import org.bouncycastle.openssl.PEMParser
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder
import java.io.StringReader
import java.security.KeyPair
import java.security.PublicKey

object PemMarshaller {

    init {
        CryptoProvider.ensureProviderRegistered()
    }

    fun parsePublicKey(pemText: String): PublicKey {
        val pemObject = parsePemText(pemText, SubjectPublicKeyInfo::class.java)

        return JcaPEMKeyConverter()
                .setProvider(CryptoProvider.NAME)
                .getPublicKey(pemObject)
    }

    fun parseKeyPair(pemText: String): KeyPair {
        val pemObject = parsePemText(pemText, PEMKeyPair::class.java)

        return JcaPEMKeyConverter()
                .setProvider(CryptoProvider.NAME)
                .getKeyPair(pemObject)
    }

    fun parseEncryptedKeyPair(pemText: String,
                              password: String): KeyPair {
        val pemObject = parsePemText(pemText, PEMEncryptedKeyPair::class.java)

        val pemDecryptorProvider: PEMDecryptorProvider = JcePEMDecryptorProviderBuilder()
                .setProvider(CryptoProvider.NAME)
                .build(password.toCharArray())

        val pemKeyPair = pemObject.decryptKeyPair(pemDecryptorProvider)

        return JcaPEMKeyConverter()
                .setProvider(CryptoProvider.NAME)
                .getKeyPair(pemKeyPair)
    }

    private fun <T : Any> parsePemText(pemText: String,
                                       objectType: Class<T>): T {
        val pemObject = (PEMParser(StringReader(pemText)).use { it.readObject() }
                ?: throw IllegalArgumentException("the given text is not in PEM format"))

        if (objectType.isAssignableFrom(pemObject.javaClass)) {
            return objectType.cast(pemObject)
        }

        throw IllegalArgumentException("the PEM does not hold an [${getPemTypeDescription(objectType)}], but an [${getPemTypeDescription(pemObject.javaClass)}]")
    }

    private fun getPemTypeDescription(pemObjectClass: Class<*>): String {
        if (SubjectPublicKeyInfo::class.java.isAssignableFrom(pemObjectClass)) {
            return "public key"
        }
        if (PEMKeyPair::class.java.isAssignableFrom(pemObjectClass)) {
            return "unencrypted key pair"
        }

        if (PEMEncryptedKeyPair::class.java.isAssignableFrom(pemObjectClass)) {
            return "encrypted key pair"
        }

        return "unknown type {${pemObjectClass.name}}"
    }

}
