package com.testerum.cloud_client.licenses.model.get_updated_licenses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GetUpdatedLicensesRequestItem @JsonCreator constructor(@JsonProperty("email")             val email: String,
                                                                  @JsonProperty("passwordHash")      val passwordHash: String,
                                                                  @JsonProperty("existingLicenseId") val existingLicenseId: String)
