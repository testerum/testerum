package com.testerum.model.user.license

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class UserLicenseInfo @JsonCreator constructor(@JsonProperty("email")               val email: String,
                                                    @JsonProperty("firstName")           val firstName: String?,
                                                    @JsonProperty("lastName")            val lastName: String?,
                                                    @JsonProperty("creationDate")        val creationDate: LocalDate,
                                                    @JsonProperty("expirationDate")      val expirationDate: LocalDate,
                                                    @JsonProperty("daysUntilExpiration") val daysUntilExpiration: Int,
                                                    @JsonProperty("expired")             val expired: Boolean)
