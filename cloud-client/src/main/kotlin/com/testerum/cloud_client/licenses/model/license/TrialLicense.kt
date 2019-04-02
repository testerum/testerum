package com.testerum.cloud_client.licenses.model.license

import java.time.LocalDate

data class TrialLicense(override val id: String,
                        override val creationDateUtc: LocalDate,
                        override val expirationDateUtc: LocalDate) : License
