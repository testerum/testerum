package com.testerum.web_backend.services.filesystem

import com.testerum.common_jdk.toStringWithStacktrace
import com.testerum.common_kotlin.canCreateChild
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.exists
import com.testerum.common_kotlin.hasSubDirectories
import com.testerum.common_kotlin.isDirectory
import com.testerum.common_kotlin.isRegularFile
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.config.dir_tree.CreateFileSystemDirectoryRequest
import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.config.dir_tree.FileSystemEntry
import com.testerum.model.config.dir_tree.FileSystemFile
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.model.infrastructure.path.PathInfo
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

        private val FS_ENTRIES_COMPARATOR: Comparator<FileSystemEntry> = compareBy<FileSystemEntry>(
                { if (it is FileSystemDirectory) 0 else 1 }, // directories before files
                { it.name.lowercase() }
        )
    }

    fun getDirectoryTree(pathAsString: String, showFiles:Boolean): FileSystemDirectory {
        return if (pathAsString == "") {
            getRoot(showFiles)
        } else {
            getSub(
                    Paths.get(pathAsString),
                    showFiles
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
                hasChildren = directoryToCreate.hasSubDirectories
        )
    }

    private fun getRoot(showFiles:Boolean) : FileSystemDirectory {
        val rootDirectories = getRootDirectories(showFiles).sortedBy { it.toString() }

        return FileSystemDirectory(
                name = "",
                absoluteJavaPath = "",
                isProject = false,
                canCreateChild = false,
                hasChildren = rootDirectories.isNotEmpty(),
                children = rootDirectories
        )
    }

    private fun getRootDirectories(showFiles:Boolean): List<FileSystemDirectory> {
        return FileSystems.getDefault().rootDirectories.map { getSub(it, showFiles) }
    }

    private fun getSub(dir: JavaPath, showFiles:Boolean): FileSystemDirectory {
        val childrenDirectories = getSubDirectoriesOrFiles(dir, showFiles).sortedWith(FS_ENTRIES_COMPARATOR)

        return FileSystemDirectory(
                name = dir.fileName?.toString() ?: dir.toString(),
                absoluteJavaPath = dir.toAbsolutePath().normalize().toString(),
                isProject = testerumProjectFileService.isTesterumProject(dir),
                canCreateChild = dir.canCreateChild,
                hasChildren = childrenDirectories.isNotEmpty(),
                children = childrenDirectories
        )
    }

    private fun getSubDirectoriesOrFiles(dir: JavaPath, showFiles:Boolean): List<FileSystemEntry> {
        val result = mutableListOf<FileSystemEntry>()

        dir.listSafely().use { pathStream ->
            pathStream.forEach { path ->
                if (path.isDirectory) {
                    result += FileSystemDirectory(
                            name = path.fileName.toString(),
                            absoluteJavaPath = path.toAbsolutePath().normalize().toString(),
                            isProject = testerumProjectFileService.isTesterumProject(path),
                            canCreateChild = path.canCreateChild,
                            hasChildren = path.hasSubDirectories
                    )
                } else if (path.isRegularFile && showFiles) {
                    result += FileSystemFile(
                            name = path.fileName.toString(),
                            absoluteJavaPath = path.toAbsolutePath().normalize().toString()
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

    fun getPathInfo(pathAsString: String): PathInfo {
        val path: java.nio.file.Path?
        try {
            path = Paths.get(pathAsString)
        } catch (e: Exception) {
            return PathInfo(
                    pathAsString,
                    isValidPath = false,
                    isExistingPath = false,
                    isProjectDirectory = false,
                    canCreateChild = false
            )
        }

        if (!path.exists) {
            return PathInfo(
                    pathAsString,
                    isValidPath = true,
                    isExistingPath = false,
                    isProjectDirectory = false,
                    canCreateChild = path.canCreateChild
            )
        }

        return PathInfo(
                pathAsString,
                isValidPath = true,
                isExistingPath = true,
                isProjectDirectory = testerumProjectFileService.isTesterumProject(path),
                canCreateChild = path.canCreateChild
        )
    }
}
