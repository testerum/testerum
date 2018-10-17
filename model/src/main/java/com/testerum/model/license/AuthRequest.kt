package com.testerum.model.license

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path

data class AuthRequest @JsonCreator constructor(@JsonProperty("email") val email: String,
                                                @JsonProperty("password") val password: String)
