package com.testerum.cloud_client.licenses.model.get_updated_licenses

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class GetUpdatedLicensesResponseItem @JsonCreator constructor(@JsonProperty("status")                           val status: GetUpdatedLicenseStatus,
                                                                   @JsonProperty("updatedSignedLicensedUserProfile") val updatedSignedLicensedUserProfile: String?)
