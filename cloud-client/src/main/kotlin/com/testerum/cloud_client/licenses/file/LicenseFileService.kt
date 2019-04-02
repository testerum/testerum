package com.testerum.cloud_client.licenses.file

import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.cloud_client.licenses.model.user.User
import com.testerum.cloud_client.licenses.parser.SignedUserParser
import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Path as JavaPath

class LicenseFileService(private val signedUserParser: SignedUserParser) {

    fun save(signedUser: String,
             licensesDir: JavaPath): User {
        val user = signedUserParser.parse(signedUser)

        val fileName = URLEncoder.encode(
                user.email,
                Charsets.UTF_8.name()
        )

        val filePath = licensesDir.resolve(fileName)

        filePath.parent.createDirectories()

        Files.write(
                filePath,
                signedUser.toByteArray(Charsets.UTF_8)
        )

        return user
    }

    fun getUsers(licensesDir: JavaPath): List<User> {
        val result = mutableListOf<User>()

        val licenseFiles: List<JavaPath> = licensesDir.walkAndCollect { true }
        for (licenseFile in licenseFiles) {
            try {
                val signedUser = String(
                        Files.readAllBytes(licenseFile),
                        Charsets.UTF_8
                )

                result += signedUserParser.parse(signedUser)
            } catch (ignored: Exception) {
                // ignore invalid license files
            }
        }

        return result
    }

}