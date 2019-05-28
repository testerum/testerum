package com.testerum.model.user.license

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class TrialLicenceInfo @JsonCreator constructor(@JsonProperty("startDate")           val startDate: LocalDate, // start date of the trial or expiration period (depending on the "expired" field)
                                                     @JsonProperty("endDate")             val endDate: LocalDate,   // end date of the trial or expiration period (depending on the "expired" field)
                                                     @JsonProperty("daysUntilExpiration") val daysUntilExpiration: Int,
                                                     @JsonProperty("expired")             val expired: Boolean)
