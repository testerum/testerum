package com.testerum.model.feature

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.testerum.model.feature.hooks.Hooks
import com.testerum.model.file.Attachment
import com.testerum.model.infrastructure.path.HasPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.util.escape

data class Feature @JsonCreator constructor(@JsonProperty("name") val name: String,
                                            @JsonProperty("path") override val path: Path,
                                            @JsonProperty("oldPath") val oldPath: Path? = path,
                                            @JsonProperty("description") val description: String? = null,
                                            @JsonProperty("tags") val tags: List<String> = emptyList(),
                                            @JsonProperty("attachments") val attachments: List<Attachment> = emptyList(),
                                            @JsonProperty("hooks") val hooks: Hooks = Hooks.EMPTY) : HasPath {

    private val _id = path.toString()

    val id: String
        get() = _id

    private val _descendantsHaveWarnings: Boolean = hooks.beforeAll.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }
                                                 || hooks.beforeEach.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }
                                                 || hooks.afterEach.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }
                                                 || hooks.afterAll.any { it.warnings.isNotEmpty() || it.descendantsHaveWarnings }

    @get:JsonProperty("descendantsHaveWarnings")
    val descendantsHaveWarnings: Boolean
        get() = _descendantsHaveWarnings

    @JsonIgnore
    fun getNewPath(): Path {
        val directories = path.directories
        val newDirectories = mutableListOf<String>()

        if (directories.isNotEmpty()) {
            newDirectories.addAll(
                    directories.subList(0, directories.size - 1)
            )
        }
        newDirectories.add(name)

        return path.copy(
                directories = newDirectories
        )
    }

    override fun toString() = "Feature(name=$name, path=$path)"

    companion object {
        /**
         * Creates a Feature instance for a directory that doesn't have an "info.feat" file.
         */
        fun createVirtualFeature(path: Path): Feature {
            val escapedPath = path.withoutFile().escape()

            return Feature(
                name = escapedPath.directories.lastOrNull() ?: "",
                path = escapedPath
            )
        }
    }
}
