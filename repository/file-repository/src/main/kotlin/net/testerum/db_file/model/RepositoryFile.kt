package net.testerum.db_file.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import net.qutester.model.repository.enums.FileType
import net.qutester.model.resources.ResourceContext

data class RepositoryFile @JsonCreator constructor(
        /** Full path to the file (path + name + extension). Acts as ID*/
        @JsonProperty("knownPath") val knownPath: KnownPath,
        @JsonProperty("body") val body: String) {

    fun mapToResource(): ResourceContext {
        return ResourceContext(
                knownPath.asPath(),
                knownPath.asPath(),
                body
        )
    }
}

fun ResourceContext.mapToRepositoryFile(): RepositoryFile {
    var resourceFileType: FileType? = null;
    for (fileType in FileType.values()) {
        if(this.path.toString().startsWith(fileType.relativeRootDirectory.toString())) {
            resourceFileType = fileType;
        }
    }

    if (resourceFileType == null) {
        throw RuntimeException("No FileType found for the resource with the path ${this.path.toString()}")
    }

    return RepositoryFile(
            KnownPath(this.path, resourceFileType),
            this.body
    )
}