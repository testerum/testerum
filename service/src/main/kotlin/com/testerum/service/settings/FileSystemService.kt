package com.testerum.service.settings

import com.testerum.model.config.dir_tree.FileSystemDirectory
import java.nio.file.AccessDeniedException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream

class FileSystemService {

    fun getDirectoryTree(pathAsString: String): FileSystemDirectory {
        return if (pathAsString == "") {
            getRoot()
        } else {
            getSub(
                    Paths.get(pathAsString)
            )
        }
    }

    private fun getRoot() : FileSystemDirectory {
        val rootDirectories = getRootDirectories().sortedBy { it.toString() }

        return FileSystemDirectory(
                name = "",
                absoluteJavaPath = "",
                hasChildrenDirectories = rootDirectories.isNotEmpty(),
                childrenDirectories = rootDirectories
        )
    }

    private fun getRootDirectories(): List<FileSystemDirectory> {
        return FileSystems.getDefault().rootDirectories.map { rootDir ->
            FileSystemDirectory(
                    name = rootDir.toString(),
                    absoluteJavaPath = rootDir.toAbsolutePath().normalize().toString(),
                    hasChildrenDirectories = rootDir.hasSubDirectories
            )
        }
    }

    private fun getSub(dir: java.nio.file.Path): FileSystemDirectory {
        val childrenDirectories = getSubDirectories(dir).sortedBy { it.toString() }

        return FileSystemDirectory(
                name = dir.fileName?.toString() ?: dir.toString(),
                absoluteJavaPath = dir.toAbsolutePath().normalize().toString(),
                hasChildrenDirectories = childrenDirectories.isNotEmpty(),
                childrenDirectories = childrenDirectories
        )
    }

    private fun getSubDirectories(dir: java.nio.file.Path): List<FileSystemDirectory> {
        val result = mutableListOf<FileSystemDirectory>()

        dir.list().use { pathStream ->
            pathStream.forEach { path ->
                result += FileSystemDirectory(
                        name = path.fileName.toString(),
                        absoluteJavaPath = path.toAbsolutePath().normalize().toString(),
                        hasChildrenDirectories = path.hasSubDirectories
                )
            }
        }

        return result
    }

    private val java.nio.file.Path.hasSubDirectories: Boolean
            get() {
                if (!Files.isDirectory(this)) {
                    return false
                }

                return try {
                    Files.list(this).use { pathStream ->
                        return pathStream.findFirst().isPresent
                    }
                } catch (e: AccessDeniedException) {
                    false
                }
            }

    private fun java.nio.file.Path.list(): Stream<java.nio.file.Path> = Files.list(this)
}
