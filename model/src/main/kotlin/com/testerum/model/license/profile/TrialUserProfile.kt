package com.testerum.model.license.profile

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

class TrialUserProfile @JsonCreator constructor(@JsonProperty("startDateUtc") val startDateUtc: LocalDate,
                                                @JsonProperty("endDateUtc")   val endDateUtc: LocalDate) : UserProfile
