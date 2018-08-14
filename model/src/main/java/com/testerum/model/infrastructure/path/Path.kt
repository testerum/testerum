package com.testerum.model.infrastructure.path

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.nio.file.Paths

data class Path @JsonCreator constructor(
        @JsonProperty("directories") val directories: List<String>,
        @JsonProperty("fileName") val fileName: String?,
        @JsonProperty("fileExtension") val fileExtension: String?) {

    companion object {
        val EMPTY = Path(emptyList(), null, null)

        fun createInstance(pathAsString: String): Path {

            var pathsPart = pathAsString.split("/", "\\")
            var fileName:String? = null
            var extension:String? = null

            val lastPathPart = pathsPart.last().toString()
            if (lastPathPart.contains('.')) {
                pathsPart = pathsPart.dropLast(1)
                fileName = lastPathPart.substringBefore(".")
                extension = lastPathPart.substringAfter(".")
            }

            return Path(pathsPart, fileName, extension)
        }
    }

    @JsonIgnore
    val fullFilename: String? = if (fileName == null && fileExtension == null) {
        null
    } else {
        buildString {
            if (fileName != null) {
                append(fileName)
            }
            if (fileExtension != null) {
                append(".")
                append(fileExtension)
            }
        }
    }

    @JsonIgnore
    val parts: List<String> = run {
        if (fullFilename == null) {
            directories
        } else {
            val result = directories.toMutableList()

            result.add(fullFilename)

            result
        }
    }

    @JsonIgnore
    fun toJavaPath(): java.nio.file.Path {
        return Paths.get(this.toString())
    }

    @JsonIgnore
    fun toJavaAbsolutePath(): java.nio.file.Path {
        return Paths.get("/" + this.toString())
    }

    override fun toString(): String = parts.joinToString(separator = "/")

    @JsonIgnore
    fun isFile(): Boolean {
        return fileName != null || fileExtension != null
    }

    @JsonIgnore
    @Suppress("unused")
    fun isDirectory(): Boolean {
        return !isFile()
    }

    @JsonIgnore
    fun resolve(path: Path): Path {
        if (this.isFile()) {
            throw RuntimeException("Can't resolve relative path to a file")
        }

        return Path(this.directories + path.directories, path.fileName, path.fileExtension)
    }

    @JsonIgnore
    fun isEmpty(): Boolean {
        return directories.isEmpty() && fileName == null && fileExtension == null
    }
}

fun java.nio.file.Path.toMyPath(): Path {
    return Path.createInstance(this.toString())
}
