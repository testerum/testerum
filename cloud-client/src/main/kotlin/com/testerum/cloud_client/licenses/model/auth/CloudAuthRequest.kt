package com.testerum.cloud_client.licenses.model.auth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CloudAuthRequest @JsonCreator constructor(@JsonProperty("email") val email: String,
                                                     @JsonProperty("password") val password: String)
