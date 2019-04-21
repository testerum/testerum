package com.testerum.web_backend.services.user.security.dao

import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.web_backend.services.user.security.model.TesterumUser

class LicenseCacheTesterumUserDao(private val licensesCache: LicensesCache) : TesterumUserDao {

    override fun getUserByEmail(email: String): TesterumUser? {
        val userProfile = licensesCache.getLicenseByEmail(email)
                ?: return null

        return TesterumUser(
                email = userProfile.assigneeEmail,
                passwordHash = userProfile.passwordHash
        )
    }

}
