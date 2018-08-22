package com.testerum.service.file_repository.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext

data class RepositoryFileChange @JsonCreator constructor(
        @JsonProperty("oldKnownPath") val oldKnownPath: KnownPath?,
        @JsonProperty("repositoryFile") val repositoryFile: RepositoryFile)

fun ResourceContext.mapToRepositoryFileChange(fileType: FileType): RepositoryFileChange {
    return RepositoryFileChange(
            if (oldPath != null) KnownPath(oldPath!!, fileType) else null,
            RepositoryFile(
                    KnownPath(this.path, fileType),
                    this.body
            )
    )
}
