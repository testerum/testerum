package com.testerum.model.license.auth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.license.info.UserLicenseInfo

data class AuthResponse @JsonCreator constructor(@JsonProperty("authToken") val authToken: String,
                                                 @JsonProperty("currentUserLicense") val currentUserLicense: UserLicenseInfo)
