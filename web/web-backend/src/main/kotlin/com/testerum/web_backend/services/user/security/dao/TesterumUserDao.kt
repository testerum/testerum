package com.testerum.web_backend.services.user.security.dao

import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile

interface TesterumUserDao {

    fun getUserByEmail(email: String): LicensedUserProfile?

}
