package net.qutester.model.resources.rdbms.connection

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RdbmsDriver @JsonCreator constructor(
        @JsonProperty("name") val name: String,

        @JsonProperty("driverJar") val driverJar: String,
        @JsonProperty("driverClass") val driverClass: String,

        @JsonProperty("urlPattern") val urlPattern: String,
        @JsonProperty("listSchemasQuery") val listSchemasQuery: String? = ""
)