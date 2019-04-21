package com.testerum.web_backend.filter.security

import com.testerum.cloud_client.licenses.model.license.LicensedUserProfile

object CurrentUserHolder {

    private val currentUser = ThreadLocal<LicensedUserProfile>()

    fun set(currentUser: LicensedUserProfile?) {
        this.currentUser.set(currentUser)
    }

    fun clear() {
        this.currentUser.remove()
    }

    fun get(): LicensedUserProfile? = currentUser.get()

}
