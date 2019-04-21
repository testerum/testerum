package com.testerum.web_backend.services.user.security.dao

import com.testerum.cloud_client.licenses.cache.LicensesCache
import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile

class LicenseCacheTesterumUserDao(private val licensesCache: LicensesCache) : TesterumUserDao {

    override fun getUserByEmail(email: String): LicensedUserProfile? {
        return licensesCache.getLicenseByEmail(email)
    }

}
