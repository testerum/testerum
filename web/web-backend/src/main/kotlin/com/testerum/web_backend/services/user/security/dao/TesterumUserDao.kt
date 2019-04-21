package com.testerum.web_backend.services.user.security.dao

import com.testerum.web_backend.services.user.security.model.TesterumUser

interface TesterumUserDao {

    fun getUserByEmail(email: String): TesterumUser?

}
