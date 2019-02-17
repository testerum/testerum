package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteIfExists
import com.testerum.common_kotlin.deleteRecursivelyIfExists
import com.testerum.common_kotlin.exists
import com.testerum.common_kotlin.getContent
import com.testerum.common_kotlin.hasExtension
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.common_kotlin.walk
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.BusinessToFileStepMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessStepMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.exception.model.ValidationModel
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.step.ComposedStepDef
import com.testerum.model.step.ComposedStepDef.Companion.COMPOSED_STEP_FILE_EXTENSION
import com.testerum.test_file_format.stepdef.FileStepDef
import com.testerum.test_file_format.stepdef.FileStepDefParserFactory
import com.testerum.test_file_format.stepdef.FileStepDefSerializer
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.Path as JavaPath

class ComposedStepFileService(private val fileToBusinessStepMapper: FileToBusinessStepMapper,
                              private val businessToFileStepMapper: BusinessToFileStepMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ComposedStepFileService::class.java)

        private val COMPOSED_STEP_PARSER = ParserExecuter(FileStepDefParserFactory.stepDef())
        private val COMPOSED_STEP_SERIALIZER = FileStepDefSerializer
    }

    fun getAllComposedSteps(composedStepsDir: JavaPath): List<ComposedStepDef> {
        val composedSteps = mutableListOf<ComposedStepDef>()

        val absoluteComposedStepsDir = composedStepsDir.toAbsolutePath().normalize()
        absoluteComposedStepsDir.walk { path ->
            if (path.isComposedStepDef) {
                val fileStepDef = parseComposedStepDefFileSafely(path)

                if (fileStepDef != null) {
                    val relativePath = absoluteComposedStepsDir.relativize(path)
                    val composedStepDef = fileToBusinessStepMapper.mapStepDef(fileStepDef, relativePath)

                    composedSteps += composedStepDef
                }
            }
        }

        return composedSteps
    }

    private fun getComposedStepAtPath(path: Path,
                                      composedStepsDir: JavaPath): ComposedStepDef {
        val escapedPath = path.escape()

        val composedStepFile = composedStepsDir.resolve(
                escapedPath.toString()
        )

        val fileStepDef = parseComposedStepDefFile(composedStepFile)

        val absoluteComposedStepsDir = composedStepsDir.toAbsolutePath().normalize()
        val relativePath = absoluteComposedStepsDir.relativize(composedStepFile)
        val composedStepDef = fileToBusinessStepMapper.mapStepDef(fileStepDef, relativePath)

        return composedStepDef

    }

    private val JavaPath.isComposedStepDef: Boolean
        get() = isRegularFile && hasExtension(".$COMPOSED_STEP_FILE_EXTENSION")

    private fun parseComposedStepDefFileSafely(file: JavaPath): FileStepDef? {
        return try {
            parseComposedStepDefFile(file)
        } catch (e: Exception) {
            LOG.warn("failed to load composed step at [${file.toAbsolutePath().normalize()}]", e)

            null
        }
    }

    private fun parseComposedStepDefFile(file: java.nio.file.Path): FileStepDef {
        return COMPOSED_STEP_PARSER.parse(
                file.getContent()
        )
    }

    fun deleteComposedStep(path: Path, composedStepsDir: JavaPath) {
        val escapedPath = path.escape()
        val composedStepFile = composedStepsDir.resolve(escapedPath.toString())

        composedStepFile.deleteIfExists()
    }

    fun save(composedStep: ComposedStepDef, composedStepsDir: JavaPath): ComposedStepDef {
        val oldPath = composedStep.oldPath
        val newEscapedPath = composedStep.getNewPath().escape()

        val oldStepFile: JavaPath? = oldPath?.let {
            composedStepsDir.resolve(oldPath.toString())
        }
        val newStepFile: JavaPath = composedStepsDir.resolve(newEscapedPath.toString())

        // handle rename
        oldStepFile?.smartMoveTo(
                newStepFile,
                createDestinationExistsException = {
                    ValidationException(
                            globalMessage = "The step at path [$newEscapedPath] already exists",
                            globalHtmlMessage = "The step at path<br/><code>$newEscapedPath</code><br/>already exists"
                    )
                }
        )

        // write the new step file
        newStepFile.parent?.createDirectories()

        val fileComposedStep = businessToFileStepMapper.mapComposedStep(composedStep)
        val serializedFileComposedStep = COMPOSED_STEP_SERIALIZER.serializeToString(fileComposedStep)

        val validationException = COMPOSED_STEP_PARSER.validate(serializedFileComposedStep)
        if (validationException != null) {
            throw ValidationException(
                    globalMessage = "Invalid step definition: [${validationException.message}]",
                    globalHtmlMessage = "Invalid step definition:<br/><code>${validationException.message}</code>"
            )
        }

        newStepFile.parent?.createDirectories()

        Files.write(
                newStepFile,
                serializedFileComposedStep.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        return getComposedStepAtPath(newEscapedPath, composedStepsDir)
    }

    fun renameDirectory(renamePath: RenamePath, composedStepsDir: JavaPath): Path {
        val oldEscapedPath = renamePath.path.escape()

        val oldJavaDir = composedStepsDir.resolve(
                oldEscapedPath.directories.joinToString(separator = "/")
        )
        val newJavaDir = oldJavaDir.resolveSibling(
                renamePath.newName
        )
        val newRelativePath = composedStepsDir.relativize(newJavaDir)

        if (newJavaDir.exists) {
            throw ValidationException(
                    ValidationModel(
                            globalMessage = "the directory at path [$newRelativePath] already exists"
                    )
            )
        }

        if (oldJavaDir.exists) {
            Files.move(oldJavaDir, newJavaDir)
        } else {
            LOG.warn("ignoring attempt to rename directory that doesn't exist [${oldJavaDir.toAbsolutePath().normalize()}]")
        }

        return Path.createInstance(
                newRelativePath.toString()
        )
    }

    fun deleteDirectory(path: Path, composedStepsDir: JavaPath) {
        val escapedPath = path.escape()

        val javaDir = composedStepsDir.resolve(escapedPath.toString())

        javaDir.deleteRecursivelyIfExists()
    }

    /**
     * * ``copyPath.copyPath`` MUST BE a file
     * * if ``copyPath.destinationKnownPath`` is a file, this method renames ``copyPath`` to this new name
     * * if ``copyPath.destinationKnownPath`` is a directory, this method moves ``copyPath`` inside this directory
     *
     * @return the destination file name, with escapes applied
     */
    fun moveComposedStepDirectoryOrFile(copyPath: CopyPath, composedStepsDir: JavaPath): Path {
        val escapedSourceFile = copyPath.copyPath.escape()
        val escapedDestinationPath = copyPath.destinationPath.escape()

        val escapedDestinationFile = if (escapedDestinationPath.isFile()) {
            escapedDestinationPath
        } else {
            escapedDestinationPath.copy(
                    fileName = escapedSourceFile.fileName,
                    fileExtension = escapedSourceFile.fileExtension
            )
        }

        val sourceJavaFile = composedStepsDir.resolve(
                escapedSourceFile.toString()
        )
        val destinationJavaFile = composedStepsDir.resolve(
                escapedDestinationFile.toString()
        )

        sourceJavaFile.smartMoveTo(
                destinationJavaFile,
                createDestinationExistsException = {
                    ValidationException(
                            globalMessage = "The file at path [$escapedDestinationFile] already exists",
                            globalHtmlMessage = "The file at path<br/><code>$escapedDestinationFile</code><br/>already exists")
                }
        )

        return escapedDestinationFile
    }

}
