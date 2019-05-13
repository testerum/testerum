package com.testerum.model.user.license

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class TrialLicenceInfo @JsonCreator constructor(@JsonProperty("startDate") val startDate: LocalDate,
                                                     @JsonProperty("endDate")   val endDate: LocalDate,
                                                     @JsonProperty("expired")   val expired: Boolean)
