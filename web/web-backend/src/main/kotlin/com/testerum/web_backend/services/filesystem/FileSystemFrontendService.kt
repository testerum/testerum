package com.testerum.web_backend.services.filesystem

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.common_kotlin.canCreateChild
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.hasSubDirectories
import com.testerum.common_kotlin.isDirectory
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.config.dir_tree.CreateFileSystemDirectoryRequest
import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import org.slf4j.LoggerFactory
import java.nio.file.AccessDeniedException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Stream
import java.nio.file.Path as JavaPath

class FileSystemFrontendService(private val testerumProjectFileService: TesterumProjectFileService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FileSystemFrontendService::class.java)
    }

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
                                globalMessage = "Got access denied while creating directory [${directoryToCreate.toAbsolutePath().normalize()}].",
                                globalMessageDetails = e.toStringWithStacktrace()
                        )
                )
            } catch (e: Exception) {
                throw ValidationException(
                        ValidationModel(
                                globalMessage = "Failed to create directory [${directoryToCreate.toAbsolutePath().normalize()}].",
                                globalMessageDetails = e.toStringWithStacktrace()
                        )
                )
            }
        }

        return FileSystemDirectory(
                name = directoryToCreate.fileName.toString(),
                absoluteJavaPath = directoryToCreate.toAbsolutePath().normalize().toString(),
                isProject = testerumProjectFileService.isTesterumProject(directoryToCreate),
                canCreateChild = directoryToCreate.canCreateChild,
                hasChildrenDirectories = directoryToCreate.hasSubDirectories
        )
    }

    private fun getRoot() : FileSystemDirectory {
        val rootDirectories = getRootDirectories().sortedBy { it.toString() }

        return FileSystemDirectory(
                name = "",
                absoluteJavaPath = "",
                isProject = false,
                canCreateChild = false,
                hasChildrenDirectories = rootDirectories.isNotEmpty(),
                childrenDirectories = rootDirectories
        )
    }

    private fun getRootDirectories(): List<FileSystemDirectory> {
        return FileSystems.getDefault().rootDirectories.map { getSub(it) }
    }

    private fun getSub(dir: JavaPath): FileSystemDirectory {
        val childrenDirectories = getSubDirectories(dir).sortedBy { it.toString() }

        return FileSystemDirectory(
                name = dir.fileName?.toString() ?: dir.toString(),
                absoluteJavaPath = dir.toAbsolutePath().normalize().toString(),
                isProject = testerumProjectFileService.isTesterumProject(dir),
                canCreateChild = dir.canCreateChild,
                hasChildrenDirectories = childrenDirectories.isNotEmpty(),
                childrenDirectories = childrenDirectories
        )
    }

    private fun getSubDirectories(dir: JavaPath): List<FileSystemDirectory> {
        val result = mutableListOf<FileSystemDirectory>()

        dir.listSafely().use { pathStream ->
            pathStream.forEach { path ->
                if (path.isDirectory) {
                    result += FileSystemDirectory(
                            name = path.fileName.toString(),
                            absoluteJavaPath = path.toAbsolutePath().normalize().toString(),
                            isProject = testerumProjectFileService.isTesterumProject(path),
                            canCreateChild = path.canCreateChild,
                            hasChildrenDirectories = path.hasSubDirectories
                    )
                }
            }
        }

        return result
    }

    private fun JavaPath.listSafely(): Stream<JavaPath> {
        try {
            return Files.list(this)
        } catch (e: Exception) {
            LOG.warn("error while trying to list files and sub-directories of [${this.toAbsolutePath().normalize()}]; will consider the directory to be empty", e)
            return Stream.empty()
        }
    }

}
