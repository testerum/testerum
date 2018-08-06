package com.testerum.file_repository.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
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
