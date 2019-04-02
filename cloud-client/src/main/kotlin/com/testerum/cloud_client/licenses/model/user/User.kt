package com.testerum.cloud_client.licenses.model.user

import com.testerum.cloud_client.licenses.model.license.License
import java.time.LocalDate

data class User(val email: String,
                val passwordHash: String,
                val creationDateUtc: LocalDate,
                val name: String?,
                val companyName: String?,
                val assignedLicense: License,
                val managedLicenses: List<License>)
