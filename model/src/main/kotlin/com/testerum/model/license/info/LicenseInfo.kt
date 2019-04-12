package com.testerum.model.license.info

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class LicenseInfo @JsonCreator constructor(@JsonProperty("serverHasLicenses")  val serverHasLicenses: Boolean,
                                                @JsonProperty("currentUserLicense") val currentUserLicense: UserLicenseInfo?,
                                                @JsonProperty("trialLicense")       val trialLicense: TrialLicenceInfo?)
