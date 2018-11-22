package com.testerum.model.infrastructure.path

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.nio.file.Paths
import java.util.*
import java.nio.file.Path as JavaPath

data class Path @JsonCreator constructor(
        @JsonProperty("directories") val directories: List<String>,
        @JsonProperty("fileName") val fileName: String? = null,
        @JsonProperty("fileExtension") val fileExtension: String? = null) {

    companion object {
        val EMPTY = Path(emptyList(), null, null)

        fun createInstance(pathAsString: String): Path {

            var pathsPart = pathAsString.split("/", "\\")
            pathsPart = pathsPart.filter { it.isNotBlank() }
            var fileName:String? = null
            var extension:String? = null

            if (pathsPart.isNotEmpty()) {
                val lastPathPart = pathsPart.last().toString()
                if (lastPathPart.contains('.')) {
                    pathsPart = pathsPart.dropLast(1)
                    fileName = lastPathPart.substringBefore(".")
                    extension = lastPathPart.substringAfter(".")
                }
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
    fun toJavaPath(): JavaPath {
        return Paths.get(this.toString())
    }

    @JsonIgnore
    fun toJavaAbsolutePath(): JavaPath {
        return Paths.get("/" + this.toString())
    }

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
    fun isEmpty(): Boolean {
        return directories.isEmpty() && fileName == null && fileExtension == null
    }

    fun isChildOrSelf(parentPath: Path): Boolean {
        if (parentPath.isFile()) {
            return this == parentPath
        }

        val indexOfSubList = Collections.indexOfSubList(this.directories, parentPath.directories)

        return indexOfSubList == 0
    }

    fun isChildOrSelfOfAny(parentPaths: List<Path>): Boolean {
        for (parentPath in parentPaths) {
            if (isChildOrSelf(parentPath)) {
                return true
            }
        }

        return false
    }

    fun replaceDirs(oldPath: Path, newPath: Path): Path {
        val indexOfSubList = Collections.indexOfSubList(directories, oldPath.directories)
        if (indexOfSubList == -1) {
            return this
        }

        val resultDirs = mutableListOf<String>()

        resultDirs.addAll(directories.subList(0, indexOfSubList))
        resultDirs.addAll(newPath.directories)
        resultDirs.addAll(directories.subList(indexOfSubList + oldPath.directories.size, directories.size))

        return this.copy(
                directories = resultDirs
        )
    }


    fun withoutFile(): Path = this.copy(fileName = null, fileExtension = null)

    fun withoutFileExtension() = this.copy(fileExtension = null)

    override fun toString(): String = parts.joinToString(separator = "/")

}
