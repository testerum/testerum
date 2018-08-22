package com.testerum.service.file_repository

import com.testerum.service.file_repository.model.KnownPath
import com.testerum.service.file_repository.model.RepositoryFile
import com.testerum.service.file_repository.model.RepositoryFileChange
import com.testerum.model.exception.IllegalFileOperationException
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.toMyPath
import com.testerum.model.repository.enums.FileType
import com.testerum.model.resources.ResourceContext
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.function.BiPredicate


class FileRepositoryService(private val getRepositoryDirectory: () -> java.nio.file.Path?) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FileRepositoryService::class.java)

        private val REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX = Regex("[^a-zA-Z0-9-_\\s\\[\\]]")
        private const val REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS = "_"
    }

    fun getResourceByPath(knownPath: KnownPath): ResourceContext? {
        return getByPath(knownPath)?.mapToResource()
    }

    fun save(repositoryFileChange: RepositoryFileChange): RepositoryFile {
        LOGGER.debug("saving file oldPath=[{}], newPath=[{}]", repositoryFileChange.oldKnownPath, repositoryFileChange.repositoryFile.knownPath)
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val oldKnownPath = repositoryFileChange.oldKnownPath
        val newKnownPath = escapeIllegalCharactersInPath(repositoryFileChange.repositoryFile.knownPath)

        val absoluteResourcePath: java.nio.file.Path = if (oldKnownPath != newKnownPath) {
            getAbsoluteUniquePath(newKnownPath)
        } else {
            escapeAndGetAbsolutePath(newKnownPath)
        }

        createParentDirectoryIfNotExisting(absoluteResourcePath)

        Files.write(
                absoluteResourcePath,
                repositoryFileChange.repositoryFile.body.toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )

        if(oldKnownPath != null && oldKnownPath != newKnownPath) {
            Files.delete(escapeAndGetAbsolutePath(oldKnownPath))
        }

        return RepositoryFile(
                newKnownPath,
                repositoryFileChange.repositoryFile.body
        )
    }

    fun getAbsoluteUniquePath(escapedRelativeKnownPath: KnownPath): java.nio.file.Path {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val absoluteResourcePath = escapeAndGetAbsolutePath(escapedRelativeKnownPath)
        if(!Files.exists(absoluteResourcePath)) {
            return  absoluteResourcePath
        }

        var fileNameSuffixNumber = 1
        var uniqueAttemptPath = absoluteResourcePath
        while (Files.exists(uniqueAttemptPath)) {
            val uniqueAttemptKnownPath = KnownPath(
                    escapedRelativeKnownPath.directories,
                    escapedRelativeKnownPath.fileName + "-" + fileNameSuffixNumber,
                    escapedRelativeKnownPath.fileExtension,
                    escapedRelativeKnownPath.fileType
            )

            fileNameSuffixNumber ++
            uniqueAttemptPath =  escapeAndGetAbsolutePath(uniqueAttemptKnownPath)
        }

        return uniqueAttemptPath
    }

    fun getByPath (knownPath: KnownPath): RepositoryFile? {
        if (getRepositoryDirectory() == null) {
            return null
        }

        val escapedRelativeKnownPath = escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            LOGGER.warn("Couldn't load the resource from path [$absoluteResourcePath]. The resource couldn't be found!")
            return null
        }

        return RepositoryFile(
                escapedRelativeKnownPath,
                String(Files.readAllBytes(absoluteResourcePath))
        )
    }

    fun getExistingResourceAbsolutePath(knownPath: KnownPath): java.nio.file.Path? {
        if (getRepositoryDirectory() == null) {
            return null
        }

        val escapedRelativeKnownPath = escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            LOGGER.warn("Couldn't load the resource from path [$absoluteResourcePath]. The resource couldn't be found!")
            return null
        }
        return absoluteResourcePath
    }

    fun getAllResourcesByType(fileType: FileType): List<RepositoryFile> {
        if (getRepositoryDirectory() == null) {
            return emptyList()
        }

        return getAllResourcesByTypeUnderPath(
                KnownPath(
                        Path.EMPTY,
                        fileType
                )
        )
    }


    fun getAllResourcesByTypeUnderPath(parentPath: KnownPath): List<RepositoryFile> {
        if (getRepositoryDirectory() == null) {
            return emptyList()
        }

        val result = mutableListOf<RepositoryFile>()

        val rootPath = getAbsolutePath(parentPath)

        if (!Files.exists(rootPath)) {
            return emptyList()
        }

        Files.find(
                rootPath,
                Int.MAX_VALUE,
                BiPredicate { path, attr -> (attr.isRegularFile && path.toString().endsWith(parentPath.fileType.fileExtension)) }
        ).use { pathStream ->
            pathStream.forEach { path ->
                val relativePathAsString = path.toString().removePrefix(
                        rootPath.toString() + File.separator
                )

                val relativePathToRoot = getRelativePathFromAbsolutePath(rootPath.resolve(relativePathAsString), parentPath.fileType)

                result.add(
                        RepositoryFile(
                                KnownPath(relativePathToRoot.toString(), parentPath.fileType),
                                String(Files.readAllBytes(path))
                        )
                )
            }
        }

        return result
    }

    fun getAllPathsOfResourcesByType(fileType: FileType): List<Path> {
        val result = mutableListOf<Path>()

        val rootPath = getRepositoryDirectory()
                ?:return emptyList()
        val fileTypeRootPath = rootPath.resolve(fileType.relativeRootDirectory.toJavaPath())
        try {
            Files.find(
                    fileTypeRootPath,
                    Integer.MAX_VALUE,
                    BiPredicate { path, attr -> (attr.isRegularFile && path.toString().endsWith(fileType.fileExtension)) }
            ).use { pathStream ->
                pathStream.forEach {path ->
                    result.add(
                            Path.createInstance(
                                    path.toString().removePrefix(
                                            fileTypeRootPath.toString() + File.separator
                                    )
                            )
                    )
                }
            }
        } catch (e: java.nio.file.NoSuchFileException) {
            //ignore exception, the root directory might not be created yet
            //TODO: replace with IF(rootDirectoryExist) check
        }

        return result
    }

    fun existResourceAtPath(knownPath: KnownPath): Boolean {
        return escapeAndGetAbsolutePath(knownPath).toFile().exists()
    }

    fun delete(knownPath: KnownPath) {
        val absoluteResourcePath: java.nio.file.Path = escapeAndGetAbsolutePath(knownPath)

        if (absoluteResourcePath.toFile().exists()) {
            absoluteResourcePath.toFile().deleteRecursively()
        }
    }

    fun renameDirectory(knownPath: KnownPath, newName: String): Path {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val pathToRename: java.nio.file.Path = escapeAndGetAbsolutePath(knownPath)
        val newPath = pathToRename.resolveSibling(newName)

        if (pathToRename.toFile().exists()) {
            if (pathToRename.toString().equals(newPath.toString(), true) &&
                    !pathToRename.toString().equals(newPath.toString(), false)) {
                val tempPath = pathToRename.resolveSibling(UUID.randomUUID().toString())
                Files.move(pathToRename, tempPath)
                Files.move(tempPath, newPath)
            } else {
                Files.move(pathToRename, newPath)
            }
            return getRelativePathFromAbsolutePath(newPath, knownPath.fileType).toMyPath()
        }
        return getRelativePathFromAbsolutePath(newPath, knownPath.fileType).toMyPath()
    }

    /**
     * * ``copyPath`` MUST BE a file
     * * if ``destinationKnownPath`` is a file, this method renames ``copyPath`` to this new name
     * * if ``destinationKnownPath`` is a directory, this method moves ``copyPath`` inside this directory
     *
     * @return the destination file name, with escapes applied
     */
    fun moveDirectoryOrFile(copyPath: KnownPath, destinationKnownPath: KnownPath): Path {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val escapedCopyPath: KnownPath = escapeIllegalCharactersInPath(copyPath)
        val javaCopyPath: java.nio.file.Path = getAbsolutePath(escapedCopyPath)

        val escapedDestinationKnownPath: KnownPath = escapeIllegalCharactersInPath(destinationKnownPath)
        val renameFile: Boolean = escapedDestinationKnownPath.isFile()
        val destinationFileKnownPath: KnownPath = if (renameFile) {
            escapedDestinationKnownPath
        } else {
            escapedDestinationKnownPath.copy(
                    fileName = escapedCopyPath.fileName,
                    fileExtension = escapedCopyPath.fileExtension
            )
        }
        val destinationFile: java.nio.file.Path = getAbsolutePath(destinationFileKnownPath)

        Files.createDirectories(destinationFile.parent)

        try {
            Files.move(javaCopyPath, destinationFile)

            return destinationFileKnownPath.asPath()
        } catch (e: FileAlreadyExistsException) {
            val problemFileLocation = e.file
            val problemFile = File(problemFileLocation)
            val fileTypeAsString = if(problemFile.isDirectory) "Directory" else "File"

            val rootPath = getRepositoryDirectory()
            val relativeFilePath = if (rootPath != null) {
                rootPath.relativize(problemFile.toPath())
            } else {
                problemFile.toPath()
            }
            throw IllegalFileOperationException("$fileTypeAsString [$relativeFilePath] already exists", e)
        }
    }

    fun createFile(resultFilePath: KnownPath) {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val absoluteResourcePath = escapeAndGetAbsolutePath(resultFilePath)

        createParentDirectoryIfNotExisting(absoluteResourcePath)

        if (!Files.exists(absoluteResourcePath)) {
            Files.createFile(absoluteResourcePath)
        }
    }

    fun appendToFile(relativeFilePath: KnownPath, fileLogLine: String) {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val absoluteResourcePath = escapeAndGetAbsolutePath(relativeFilePath)

        Files.write(
                absoluteResourcePath,
                fileLogLine.toByteArray(UTF_8),
                StandardOpenOption.APPEND
        )
    }

    fun createParentDirectoryIfNotExisting(absoluteResourcePath: java.nio.file.Path) {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val parentDir = absoluteResourcePath.parent
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir)
        }
    }

    fun escapeAndGetAbsolutePath(knownPath: KnownPath): java.nio.file.Path {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val escapedKnownPath = escapeIllegalCharactersInPath(knownPath)

        return getAbsolutePath(escapedKnownPath)
    }

    private fun getAbsolutePath(escapedKnownPath: KnownPath): java.nio.file.Path {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val rootPath = getRepositoryDirectory()
                ?: throw unknownRepositoryDirectoryException()
        val rootDirectory = rootPath.resolve(escapedKnownPath.fileType.relativeRootDirectory.toJavaPath())

        return rootDirectory.resolve(escapedKnownPath.toString()).toAbsolutePath().normalize()
    }

    fun getRelativePathFromAbsolutePath(absolutePath: java.nio.file.Path, fileType: FileType): java.nio.file.Path {
        val rootPath = getRepositoryDirectory()
                ?: throw unknownRepositoryDirectoryException()
        val rootDirectory = rootPath.resolve(fileType.relativeRootDirectory.toJavaPath())
        val rootDirectoryAsString = rootDirectory.toString()
        val absolutePathAsString = absolutePath.toString()

        val relativePathAsString = absolutePathAsString.removePrefix(rootDirectoryAsString)
                                                              .removePrefix("\\")
                                                              .removePrefix("/")

        return java.nio.file.Paths.get(relativePathAsString)
    }

    fun escapeIllegalCharactersInPath(knownPath: KnownPath): KnownPath {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val escapedDirectories: MutableList<String> = mutableListOf()
        for (directory in knownPath.directories) {
            val escapedDir = directory.replace(
                    REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX,
                    REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS
            )
            escapedDirectories.add(escapedDir)
        }

        val escapedFileName = knownPath.fileName?.replace(
                REGEX_TO_REPLACE_ILLEGAL_FILE_REGEX,
                REPLACEMENT_CHAR_OF_ILLEGAL_FILE_CHARS
        )

        return KnownPath(escapedDirectories, escapedFileName, knownPath.fileExtension, knownPath.fileType)
    }

    fun walkDirectoryTree(type: FileType,
                          process: (rootDir: java.nio.file.Path, path: java.nio.file.Path) -> Unit) {
        if (getRepositoryDirectory() == null) {
            throw unknownRepositoryDirectoryException()
        }

        val rootPath = getAbsolutePath(KnownPath(Path.EMPTY, type))

        Files.walk(rootPath).use { pathStream ->
            pathStream.forEach { path ->
                process(rootPath, path.toAbsolutePath().normalize())
            }
        }
    }

    private fun unknownRepositoryDirectoryException() = IllegalStateException("unknown repository directory")

}
