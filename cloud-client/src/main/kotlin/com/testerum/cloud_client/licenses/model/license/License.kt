package com.testerum.cloud_client.licenses.model.license

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = TrialLicense::class, name = "TRIAL")
])
interface License {

    val id: String
    val creationDateUtc: LocalDate
    val expirationDateUtc: LocalDate

}
