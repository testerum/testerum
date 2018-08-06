package com.testerum.model.manual.runner

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.enums.ManualTestStatus

data class ManualTestExe @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                                  @JsonProperty("text") val text: String,
                                                  @JsonProperty("description") val description: String?,
                                                  @JsonProperty("tags") val tags: List<String> = emptyList(),
                                                  @JsonProperty("steps") val steps: List<ManualStepExe> = emptyList(),
                                                  @JsonProperty("testStatus") val testStatus: ManualTestStatus = ManualTestStatus.NOT_EXECUTED,
                                                  @JsonProperty("comments") val comments: String?
                                                  ) {

    private val _id = path.toString()

    val id: String
        get() = _id

}