package net.testerum.db_file

import com.testerum.api.test_context.settings.SettingsManager
import com.testerum.settings.SystemSettings
import net.qutester.exception.IllegalFileOperationException
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.toMyPath
import net.qutester.model.repository.enums.FileType
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.function.BiPredicate


class FileRepositoryService(private val settingsManager: SettingsManager) {
    private val LOG = LoggerFactory.getLogger(FileRepositoryService::class.java)

    private val REGEX_TO_REPLACE_ILEAGAL_FILE_REGEX = Regex("[^a-zA-Z0-9-_\\s\\[\\]]")
    private val REPLACEMENT_CHAR_OF_ILEAGAL_FILE_CHARS = "_"

    fun create(repositoryFileChange: RepositoryFileChange): RepositoryFile {
        val escapedRelativeKnownPath = escapeIllegalCharactersInPath(repositoryFileChange.repositoryFile.knownPath)
        val absoluteResourcePath = getAbsoluteUniquePath(escapedRelativeKnownPath)

        createParentDirectoryIfNotExisting(absoluteResourcePath)

        Files.write(
                absoluteResourcePath,
                repositoryFileChange.repositoryFile.body.toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )

        return RepositoryFile(
                escapedRelativeKnownPath,
                repositoryFileChange.repositoryFile.body
        )
    }

    fun update(repositoryFileChange: RepositoryFileChange): RepositoryFile {
        val oldKnownPath = repositoryFileChange.oldKnownPath!!
        val newKnownPath = escapeIllegalCharactersInPath(repositoryFileChange.repositoryFile.knownPath)

        val absoluteResourcePath: java.nio.file.Path
        if(oldKnownPath != newKnownPath) {
            absoluteResourcePath = getAbsoluteUniquePath(newKnownPath)
        } else {
            absoluteResourcePath = escapeAndGetAbsolutePath(newKnownPath)
        }

        createParentDirectoryIfNotExisting(absoluteResourcePath)

        Files.write(
                absoluteResourcePath,
                repositoryFileChange.repositoryFile.body.toByteArray(Charsets.UTF_8),
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )

        if(oldKnownPath != newKnownPath) {
            Files.delete(escapeAndGetAbsolutePath(oldKnownPath))
        }

        return RepositoryFile(
                newKnownPath,
                repositoryFileChange.repositoryFile.body
        )
    }

    fun getAbsoluteUniquePath(escapedRelativeKnownPath: KnownPath): java.nio.file.Path {
        val absoluteResourcePath = escapeAndGetAbsolutePath(escapedRelativeKnownPath)
        if(!Files.exists(absoluteResourcePath)) {
            return  absoluteResourcePath
        }

        var fileNameSuffixNumber: Int = 1
        var uniqueAttemptPath = absoluteResourcePath
        while (Files.exists(uniqueAttemptPath)) {
            val uniqueAttemptKnownPath = KnownPath (
                    escapedRelativeKnownPath.directories,
                    escapedRelativeKnownPath.fileName+"-"+fileNameSuffixNumber,
                    escapedRelativeKnownPath.fileExtension,
                    escapedRelativeKnownPath.fileType
            )

            fileNameSuffixNumber ++
            uniqueAttemptPath =  escapeAndGetAbsolutePath(uniqueAttemptKnownPath)
        }

        return uniqueAttemptPath
    }

    fun save(repositoryFileChange: RepositoryFileChange): RepositoryFile { //TODO: create separate methods for create and update
        if (repositoryFileChange.oldKnownPath == null) {
            return create(repositoryFileChange)
        }

        return update(repositoryFileChange)
    }

    fun getByPath (knownPath: KnownPath): RepositoryFile? {
        val escapedRelativeKnownPath = escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            LOG.warn("Couldn't load the resource from path [$absoluteResourcePath]. The resource couldn't be found!")
            return null
        }

        return RepositoryFile (
                escapedRelativeKnownPath,
                String(Files.readAllBytes(absoluteResourcePath))
        )
    }

    fun getExistingResourceAbsolutePath(knownPath: KnownPath): java.nio.file.Path? {
        val escapedRelativeKnownPath = escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            LOG.warn("Couldn't load the resource from path [$absoluteResourcePath]. The resource couldn't be found!")
            return null
        }
        return absoluteResourcePath
    }

    fun getAllResourcesByType(fileType: FileType): List<RepositoryFile> {
        return getAllResourcesByTypeUnderPath(
                KnownPath(
                        Path.createEmptyInstance(),
                        fileType
                )
        )
    }


    fun getAllResourcesByTypeUnderPath(parentPath: KnownPath): List<RepositoryFile> {
        val result = mutableListOf<RepositoryFile>()

        val rootPath = getAbsolutePath(parentPath)

        if (!Files.exists(rootPath)) {
            Files.createDirectories(rootPath)
            return emptyList()
        }

        Files.find(
                rootPath,
                Integer.MAX_VALUE,
                BiPredicate { path, attr -> (attr.isRegularFile && path.toString().endsWith(parentPath.fileType.fileExtension)) }
        ).use {
            it.forEach {
                val relativePathAsString = it.toString().removePrefix(
                        rootPath.toString() + File.separator
                )

                val relativePathToRoot = getRelativePathFromAbsolutePath(rootPath.resolve(relativePathAsString), parentPath.fileType)

                result.add(
                        RepositoryFile(
                                KnownPath(relativePathToRoot.toString(), parentPath.fileType),
                                String(Files.readAllBytes(it))
                        )
                )
            }
        }

        return result
    }

    fun getAllPathsOfResourcesByType(fileType: FileType): List<Path> {
        val result = mutableListOf<Path>()

        val rootPath = Paths.get(settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY.key))
        val fileTypeRootPath = rootPath.resolve(fileType.relativeRootDirectory.toJavaPath())
        try {
            Files.find(
                    fileTypeRootPath,
                    Integer.MAX_VALUE,
                    BiPredicate { path, attr -> (attr.isRegularFile && path.toString().endsWith(fileType.fileExtension)) }
            ).use {
                it.forEach {
                    result.add(
                            Path.createInstance(
                                    it.toString().removePrefix(
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
        val pathToRename: java.nio.file.Path = escapeAndGetAbsolutePath(knownPath)
        if (pathToRename.toFile().exists()) {
            val newPath = Files.move(pathToRename, pathToRename.resolveSibling(newName))
            return getRelativePathFromAbsolutePath(newPath, knownPath.fileType).toMyPath()
        }
        val newPath = pathToRename.resolveSibling(newName)
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

            val rootPath = Paths.get(settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY.key))
            val relativeFilePath = rootPath.relativize(problemFile.toPath())
            throw IllegalFileOperationException("$fileTypeAsString [${relativeFilePath.toString()}] already exists", e)
        }
    }

    fun createFile(resultFilePath: KnownPath) {
        val absoluteResourcePath = escapeAndGetAbsolutePath(resultFilePath)

        createParentDirectoryIfNotExisting(absoluteResourcePath)

        if (!Files.exists(absoluteResourcePath)) {
            Files.createFile(absoluteResourcePath)
        }
    }

    fun appendToFile(relativeFilePath: KnownPath, fileLogLine: String) {
        val absoluteResourcePath = escapeAndGetAbsolutePath(relativeFilePath)

        Files.write(
                absoluteResourcePath,
                fileLogLine.toByteArray(UTF_8),
                StandardOpenOption.APPEND
        )
    }

    fun createParentDirectoryIfNotExisting(absoluteResourcePath: java.nio.file.Path) {
        val parentDir = absoluteResourcePath.parent
        if (!Files.exists(parentDir)) {
            Files.createDirectories(parentDir, *arrayOf())
        }
    }

    fun escapeAndGetAbsolutePath(knownPath: KnownPath): java.nio.file.Path {
        val escapedKnownPath = escapeIllegalCharactersInPath(knownPath)

        return getAbsolutePath(escapedKnownPath)
    }

    private fun getAbsolutePath(escapedKnownPath: KnownPath): java.nio.file.Path {
        val repositoryDirPathAsString = settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY.key)
                ?: return escapedKnownPath.fileType.relativeRootDirectory.toJavaPath()

        val rootPath = Paths.get(repositoryDirPathAsString)
        val rootDirectory = rootPath.resolve(escapedKnownPath.fileType.relativeRootDirectory.toJavaPath())

        return rootDirectory.resolve(escapedKnownPath.toString())
    }

    fun getRelativePathFromAbsolutePath(absolutePath: java.nio.file.Path, fileType: FileType): java.nio.file.Path {

        val rootPath = Paths.get(settingsManager.getSettingValue(SystemSettings.REPOSITORY_DIRECTORY.key))
        val rootDirectory = rootPath.resolve(fileType.relativeRootDirectory.toJavaPath())
        val rootDirectoryAsString = rootDirectory.toString()
        val absolutePathAsString = absolutePath.toString()

        val relativePathAsString = absolutePathAsString.removePrefix(rootDirectoryAsString)
                                                              .removePrefix("\\")
                                                              .removePrefix("/")

        return java.nio.file.Paths.get(relativePathAsString)
    }

    fun escapeIllegalCharactersInPath(knownPath: KnownPath): KnownPath {
        val escapedDirectories: MutableList<String> = mutableListOf()
        for (directory in knownPath.directories) {
            val escapedDir = directory.replace(
                    REGEX_TO_REPLACE_ILEAGAL_FILE_REGEX,
                    REPLACEMENT_CHAR_OF_ILEAGAL_FILE_CHARS
            )
            escapedDirectories.add(escapedDir)
        }

        val escapedFileName = knownPath.fileName?.replace(
                REGEX_TO_REPLACE_ILEAGAL_FILE_REGEX,
                REPLACEMENT_CHAR_OF_ILEAGAL_FILE_CHARS
        )

        return KnownPath(escapedDirectories, escapedFileName, knownPath.fileExtension, knownPath.fileType)
    }
}