package com.testerum.service.settings

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.model.config.dir_tree.CreateFileSystemDirectoryRequest
import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
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

    fun createDirectory(createRequest: CreateFileSystemDirectoryRequest): FileSystemDirectory {
        val parentDirectory = Paths.get(createRequest.parentAbsoluteJavaPath)
        val directoryToCreate = parentDirectory.resolve(createRequest.name)

        if (directoryToCreate.doesNotExist) {
            try {
                Files.createDirectory(directoryToCreate)
            } catch (e: AccessDeniedException) {
                throw ValidationException(
                        ValidationModel(
                                globalValidationMessage = "Got access denied while creating directory [${directoryToCreate.toAbsolutePath().normalize()}].",
                                globalValidationMessageDetails = e.toStringWithStacktrace()
                        )
                )
            } catch (e: Exception) {
                throw ValidationException(
                        ValidationModel(
                                globalValidationMessage = "Failed to create directory [${directoryToCreate.toAbsolutePath().normalize()}].",
                                globalValidationMessageDetails = e.toStringWithStacktrace()
                        )
                )
            }
        }

        return FileSystemDirectory(
                name = directoryToCreate.fileName.toString(),
                absoluteJavaPath = directoryToCreate.toAbsolutePath().normalize().toString(),
                canCreateChild = directoryToCreate.canCreateChild,
                hasChildrenDirectories = directoryToCreate.hasSubDirectories
        )
    }

    private fun getRoot() : FileSystemDirectory {
        val rootDirectories = getRootDirectories().sortedBy { it.toString() }

        return FileSystemDirectory(
                name = "",
                absoluteJavaPath = "",
                canCreateChild = false,
                hasChildrenDirectories = rootDirectories.isNotEmpty(),
                childrenDirectories = rootDirectories
        )
    }

    private fun getRootDirectories(): List<FileSystemDirectory> {
        return FileSystems.getDefault().rootDirectories.map { rootDir ->
            FileSystemDirectory(
                    name = rootDir.toString(),
                    absoluteJavaPath = rootDir.toAbsolutePath().normalize().toString(),
                    canCreateChild = rootDir.canCreateChild,
                    hasChildrenDirectories = rootDir.hasSubDirectories
            )
        }
    }

    private fun getSub(dir: java.nio.file.Path): FileSystemDirectory {
        val childrenDirectories = getSubDirectories(dir).sortedBy { it.toString() }

        return FileSystemDirectory(
                name = dir.fileName?.toString() ?: dir.toString(),
                absoluteJavaPath = dir.toAbsolutePath().normalize().toString(),
                canCreateChild = dir.canCreateChild,
                hasChildrenDirectories = childrenDirectories.isNotEmpty(),
                childrenDirectories = childrenDirectories
        )
    }

    private fun getSubDirectories(dir: java.nio.file.Path): List<FileSystemDirectory> {
        val result = mutableListOf<FileSystemDirectory>()

        dir.list().use { pathStream ->
            pathStream.forEach { path ->
                if (path.isDirectory) {
                    result += FileSystemDirectory(
                            name = path.fileName.toString(),
                            absoluteJavaPath = path.toAbsolutePath().normalize().toString(),
                            canCreateChild = path.canCreateChild,
                            hasChildrenDirectories = path.hasSubDirectories
                    )
                }
            }
        }

        return result
    }

    private val java.nio.file.Path.canCreateChild: Boolean
        get() = isDirectory && isWritable && isExecutable // if the directory is not executable, we won't be able to create files or directories inside it

    private val java.nio.file.Path.isDirectory: Boolean
            get() = Files.isDirectory(this)

    private val java.nio.file.Path.isWritable: Boolean
            get() = Files.isWritable(this)

    private val java.nio.file.Path.isExecutable: Boolean
            get() = Files.isExecutable(this)

    private val java.nio.file.Path.doesNotExist: Boolean
        get() = !exists

    private val java.nio.file.Path.exists: Boolean
            get() = Files.exists(this)

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
