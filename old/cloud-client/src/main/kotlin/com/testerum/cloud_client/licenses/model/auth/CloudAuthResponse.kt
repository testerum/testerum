package com.testerum.cloud_client.licenses.model.auth

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class CloudAuthResponse @JsonCreator constructor(@JsonProperty("token") val token: String)
