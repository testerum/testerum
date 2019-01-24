package com.testerum.model.resources.http.response

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

class InvalidHttpResponse @JsonCreator constructor(@JsonProperty("errorMessage") val errorMessage: String) : HttpResponse
