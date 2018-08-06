package com.testerum.model.resources.rdbms.connection

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RdbmsConnectionConfig @JsonCreator constructor(
        @JsonProperty("driverName") var driverName: String,
        @JsonProperty("driverJar") var driverJar: String,
        @JsonProperty("driverClass") var driverClass: String,
        @JsonProperty("driverUrlPattern") var driverUrlPattern: String,

        @JsonProperty("isDefaultConnection") var isDefaultConnection: Boolean = false,

        @JsonProperty("host") val host: String?,
        @JsonProperty("port") val port: Int?,

        @JsonProperty("useCustomUrl") val useCustomUrl: Boolean = false,
        @JsonProperty("customUrl") val customUrl: String?,

        @JsonProperty("user") val user: String?,
        @JsonProperty("password") val password: String?,

        @JsonProperty("database") val database: String?
)