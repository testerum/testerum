package net.qutester.model.infrastructure.path

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
            var fileName:String? = null;
            var extension:String? = null;

            val lastPathPart = pathsPart.last().toString()
            if (lastPathPart.contains('.')) {
                pathsPart = pathsPart.dropLast(1)
                fileName = lastPathPart.substringBefore(".")
                extension = lastPathPart.substringAfter(".")
            }

            return Path(pathsPart.map { it.toString() }, fileName, extension)
        }
    }

    @JsonIgnore
    fun toJavaPath(): java.nio.file.Path {
        return Paths.get(this.toString())
    }

    override fun toString(): String {
        var result = "";
        for ((index, directory) in directories.withIndex()) {
            result += directory
            if (index < directories.size - 1) {
                result += "/"
            }
        }

        if (fileName != null) {
            if (result.isNotEmpty()) {
                result += "/"
            }
            result += fileName
        }
        if (fileExtension != null) {
            result += "." + fileExtension

        }
        return result
    }

    @JsonIgnore
    fun isFile(): Boolean {
        return fileName != null || fileExtension != null
    }

    @JsonIgnore
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
        return directories.isEmpty() && fileName == null && fileExtension == null;
    }
}

fun java.nio.file.Path.toMyPath(): Path {
    return Path.createInstance(this.toString())
}