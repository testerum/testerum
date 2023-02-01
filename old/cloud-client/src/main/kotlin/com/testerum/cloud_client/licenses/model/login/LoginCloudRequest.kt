package com.testerum.cloud_client.licenses.model.login

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class LoginCloudRequest @JsonCreator constructor(@JsonProperty("email") val email: String,
                                                      @JsonProperty("password") val password: String)
