package com.testerum.model.resources.rdbms.connection

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class RdbmsSchemasNames @JsonCreator constructor(@JsonProperty("schemas") val schemas: List<String> = emptyList(),
                                                      @JsonProperty("errorMessage") val errorMessage: String = "")
