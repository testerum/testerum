package com.testerum.model.user.auth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.user.license.UserLicenseInfo

data class AuthResponse @JsonCreator constructor(@JsonProperty("authToken") val authToken: String,
                                                 @JsonProperty("currentUserLicense") val currentUserLicense: UserLicenseInfo)
