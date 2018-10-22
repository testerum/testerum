package com.testerum.licenses.cache

import com.testerum.licenses.file.LicenseFileService
import com.testerum.licenses.model.user.User
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class LicensesCache(private val licenseFileService: LicenseFileService) {

    private val lock = ReentrantReadWriteLock()

    private var licensesDir: JavaPath? = null

    private var usersByEmail: Map<String, User> = emptyMap()

    fun initialize(licensesDir: JavaPath) {
        lock.write {
            this.licensesDir = licensesDir

            val users = licenseFileService.getUsers(licensesDir)

            this.usersByEmail = users.associateBy { it.email }
        }
    }

    fun save(signedUser: String): User {
        lock.write {
            val licensesDir = this.licensesDir
                    ?: throw IllegalStateException("cannot license because the licensesDir is not set")

            val user = licenseFileService.save(signedUser, licensesDir)

            initialize(licensesDir)

            return user
        }
    }

    fun getUserByEmail(email: String): User? = lock.read { usersByEmail[email] }

}