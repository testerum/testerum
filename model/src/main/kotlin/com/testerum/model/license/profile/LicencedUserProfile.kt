package com.testerum.model.license.profile

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class LicencedUserProfile @JsonCreator constructor(@JsonProperty("licenseId")         val licenseId: String,
                                                   @JsonProperty("assigneeEmail")     val assigneeEmail: String,
                                                   @JsonProperty("assigneeFirstName") val assigneeFirstName: String,
                                                   @JsonProperty("assigneeLastName")  val assigneeLastName: String,
                                                   @JsonProperty("passwordHash")      val passwordHash: String,
                                                   @JsonProperty("creationDateUtc")   val creationDateUtc: LocalDate,
                                                   @JsonProperty("expirationDateUtc") val expirationDateUtc: LocalDate)
