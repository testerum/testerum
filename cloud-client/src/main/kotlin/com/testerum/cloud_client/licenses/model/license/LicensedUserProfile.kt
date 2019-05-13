package com.testerum.cloud_client.licenses.model.license

import java.time.LocalDate

data class LicensedUserProfile(val licenseId: String,
                               val assigneeEmail: String,
                               val assigneeFirstName: String?,
                               val assigneeLastName: String?,
                               val passwordHash: String,
                               val creationDateUtc: LocalDate,
                               val expirationDateUtc: LocalDate)
