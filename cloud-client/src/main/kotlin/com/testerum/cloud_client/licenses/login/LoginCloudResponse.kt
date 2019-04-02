package com.testerum.cloud_client.licenses.login

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = FoundLoginCloudResponse::class   , name = "FOUND"),
    JsonSubTypes.Type(value = NotFoundLoginCloudResponse::class, name = "NOT_FOUND")
])
sealed class LoginCloudResponse

data class FoundLoginCloudResponse(val signedUser: String) : LoginCloudResponse()

class NotFoundLoginCloudResponse : LoginCloudResponse()
