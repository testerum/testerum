package com.testerum.licenses.model.user

import com.testerum.licenses.model.license.License
import java.time.LocalDate

data class User(val email: String,
                val passwordHash: String,
                val creationDateUtc: LocalDate,
                val name: String?,
                val companyName: String?,
                val assignedLicense: License,
                val managedLicenses: List<License>)
