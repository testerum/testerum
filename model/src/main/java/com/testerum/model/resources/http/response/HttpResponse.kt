package com.testerum.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ValidHttpResponse::class   , name = "VALID"),
    JsonSubTypes.Type(value = InvalidHttpResponse::class   , name = "INVALID")
])
interface HttpResponse
