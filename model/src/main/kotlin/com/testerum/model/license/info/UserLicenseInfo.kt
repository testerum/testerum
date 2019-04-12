package com.testerum.model.license.info

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class UserLicenseInfo @JsonCreator constructor(@JsonProperty("email")          val email: String,
                                                    @JsonProperty("firstName")      val firstName: String?,
                                                    @JsonProperty("lastName")       val lastName: String?,
                                                    @JsonProperty("creationDate")   val creationDate: LocalDate,
                                                    @JsonProperty("expirationDate") val expirationDate: LocalDate,
                                                    @JsonProperty("expired")        val expired: Boolean)
