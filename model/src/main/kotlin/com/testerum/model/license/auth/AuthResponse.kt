package com.testerum.model.license.auth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class AuthResponse @JsonCreator constructor(@JsonProperty("email") val email: String,
                                                 @JsonProperty("name") val name: String?,
                                                 @JsonProperty("companyName") val companyName: String?,
                                                 @JsonProperty("licenseExpirationDate") val licenseExpirationDate: LocalDate,
                                                 @JsonProperty("authToken") val authToken: String)
