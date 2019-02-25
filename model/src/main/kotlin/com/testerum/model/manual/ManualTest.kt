package com.testerum.model.manual

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.manual.enums.ManualTestStatus

data class ManualTest @JsonCreator constructor(@JsonProperty("path") val path: Path,
                                               @JsonProperty("oldPath") val oldPath: Path? = path,
                                               @JsonProperty("name") val name: String,
                                               @JsonProperty("description") val description: String?,
                                               @JsonProperty("tags") val tags: List<String> = emptyList(),
                                               @JsonProperty("stepCalls") val stepCalls: List<ManualStepCall> = emptyList(),
                                               @JsonProperty("status") val status: ManualTestStatus,
                                               @JsonProperty("comments") val comments: String?,
                                               @JsonProperty("isFinalized") val isFinalized: Boolean? // calculated: true, if the entire plan is finalized
                                               ) {

    companion object {
        val MANUAL_TEST_FILE_EXTENSION = "manual_test"
    }


    @JsonIgnore
    fun getNewPath(): Path {
        return path.copy(
                fileName = name,
                fileExtension = MANUAL_TEST_FILE_EXTENSION
        )
    }

}
