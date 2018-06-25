package net.testerum.db_file.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext

data class RepositoryFileChange @JsonCreator constructor(
        @JsonProperty("oldKnownPath") val oldKnownPath: KnownPath?,
        @JsonProperty("repositoryFile") val repositoryFile: RepositoryFile) {

    fun mapToResource(): ResourceContext {
        return ResourceContext(
                repositoryFile.knownPath.asPath(),
                oldKnownPath?.asPath(),
                repositoryFile.body
        )
    }
}

fun ResourceContext.mapToRepositoryFileChange(fileType: FileType): RepositoryFileChange {
    return RepositoryFileChange(
            if(oldPath != null) KnownPath(oldPath!!, fileType) else null,
            RepositoryFile(
                    KnownPath(this.path, fileType),
                    this.body
            )
    )
}