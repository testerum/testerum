package com.testerum.file_service.file

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.serializing.Serializer
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.deleteIfExists
import com.testerum.common_kotlin.deleteRecursivelyIfExists
import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.getBasicFileAttributes
import com.testerum.common_kotlin.getContent
import com.testerum.common_kotlin.isRegularFile
import com.testerum.common_kotlin.smartMoveTo
import com.testerum.common_kotlin.walk
import com.testerum.common_kotlin.walkAndCollect
import com.testerum.file_service.file.util.escape
import com.testerum.file_service.mapper.business_to_file.BusinessToFileFeatureMapper
import com.testerum.file_service.mapper.file_to_business.FileToBusinessFeatureMapper
import com.testerum.model.exception.ValidationException
import com.testerum.model.feature.Feature
import com.testerum.model.file.Attachment
import com.testerum.model.file.FileToUpload
import com.testerum.model.infrastructure.path.Path
import com.testerum.test_file_format.feature.FileFeature
import com.testerum.test_file_format.feature.FileFeatureParserFactory
import com.testerum.test_file_format.feature.FileFeatureSerializer
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.nio.file.Path as JavaPath

class FeatureFileService(private val featureMapper: FileToBusinessFeatureMapper,
                         private val businessToFileFeatureMapper: BusinessToFileFeatureMapper) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FeatureFileService::class.java)

        private const val FEATURE_FILE_NAME = "info.feat"
        private const val ATTACHMENTS_DIR = "_attachments"

        private val FEATURE_PARSER: ParserExecuter<FileFeature> = ParserExecuter(FileFeatureParserFactory.feature())
        private val FEATURE_SERIALIZER: Serializer<FileFeature> = FileFeatureSerializer
    }

    fun getAllFeatures(featuresDir: JavaPath): List<Feature> {
        val features = mutableListOf<Feature>()

        val absoluteFeaturesDir = featuresDir.toAbsolutePath().normalize()
        absoluteFeaturesDir.walk { path ->
            if (path.isFeature) {
                val fileFeature = parseFeatureFile(path)

                if (fileFeature != null) {
                    val relativePath = absoluteFeaturesDir.relativize(path)
                    val feature = featureMapper.mapFeature(fileFeature, relativePath)

                    features += feature
                }
            }
        }

        return features
    }

    private val JavaPath.isFeature: Boolean
        get() = isRegularFile && fileName.toString() == FEATURE_FILE_NAME

    private fun parseFeatureFile(file: JavaPath): FileFeature? {
        return try {
            FEATURE_PARSER.parse(
                    file.getContent()
            )
        } catch (e: Exception) {
            // todo: return InvalidFeature instead, that has a path and an error message
            LOG.warn("failed to load feature at [${file.toAbsolutePath().normalize()}]", e)

            null
        }
    }

    /**
     * Creates a Feature instance for a directory that doesn't have an "info.feat" file.
     */
    fun createVirtualFeature(path: Path): Feature {
        val escapedPath = path.withoutFile().escape()

        return Feature(
                name = escapedPath.directories.lastOrNull() ?: "",
                path = escapedPath
        )
    }

    fun save(feature: Feature, featuresDir: JavaPath): Feature {
        val oldEscapedPath = feature.oldPath?.escape()
        val newEscapedPath = feature.getNewPath().escape()

        val oldFeatureDir: JavaPath? = oldEscapedPath?.let {
            featuresDir.resolve(
                    it.directories.joinToString(separator = "/")
            ).toAbsolutePath().normalize()
        }
        val newFeatureDir: JavaPath = featuresDir.resolve(
                newEscapedPath.directories.joinToString(separator = "/")
        ).toAbsolutePath().normalize()

        // handle rename
        oldFeatureDir?.smartMoveTo(
                newFeatureDir,
                createDestinationExistsException = {
                    val featureDirPath = newEscapedPath.copy(fileName = null, fileExtension = null)

                    ValidationException("The feature at path<br/><code>$featureDirPath</code><br/>already exists")
                }
        )

        // write the new feature file
        newFeatureDir.createDirectories()

        val fileFeature = businessToFileFeatureMapper.mapFeature(feature)
        val serializedFileFeature = FEATURE_SERIALIZER.serializeToString(fileFeature)

        val newFeatureFile = newFeatureDir.resolve(FEATURE_FILE_NAME)

        newFeatureFile.parent?.createDirectories()

        Files.write(
                newFeatureFile,
                serializedFileFeature.toByteArray(Charsets.UTF_8),
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING
        )

        val newPath = Path.createInstance(
                featuresDir.relativize(newFeatureDir).toString()
        ).escape()

        return feature.copy(
                name = newFeatureDir.fileName?.toString() ?: feature.name,
                path = newPath,
                oldPath = newPath
        )
    }

    fun deleteFeature(path: Path, featuresDir: JavaPath) {
        val featureDir: JavaPath = featuresDir.resolve(
                path.escape().directories.joinToString(separator = "/")
        )

        if (featureDir.doesNotExist) {
            return
        }

        featureDir.deleteRecursivelyIfExists()
    }

    fun getAttachmentsAtPath(featurePath: Path,
                             featuresDir: JavaPath): List<Attachment> {
        val escapedPath = featurePath.escape()
        val attachmentsDir = featuresDir.resolve(escapedPath.directories.joinToString(separator = "/"))
                .resolve(ATTACHMENTS_DIR)

        if (attachmentsDir.doesNotExist) {
            return emptyList()
        }

        return attachmentsDir.walkAndCollect { it.isRegularFile }
                .map { fileToAttachment(it, featuresDir) }
    }

    fun getAttachmentAtPath(attachmentFilePath: Path,
                            featuresDir: JavaPath): Attachment? {
        val escapedPath = attachmentFilePath.escape()
        val attachmentsJavaFile = featuresDir.resolve(escapedPath.toString())

        if (attachmentsJavaFile.doesNotExist) {
            return null
        }

        return fileToAttachment(attachmentsJavaFile, featuresDir)
    }

    fun openAttachmentContentInputStream(attachmentFilePath: Path,
                                         featuresDir: JavaPath): InputStream? {
        val escapedPath = attachmentFilePath.escape()
        val attachmentsJavaFile = featuresDir.resolve(escapedPath.toString())

        if (attachmentsJavaFile.doesNotExist) {
            return null
        }

        return Files.newInputStream(attachmentsJavaFile)
    }

    fun deleteAttachment(attachmentFilePath: Path,
                         featuresDir: JavaPath) {
        val escapedPath = attachmentFilePath.escape()
        val attachmentsJavaFile = featuresDir.resolve(escapedPath.toString())

        attachmentsJavaFile.deleteIfExists()
    }

    fun uploadAttachment(featurePath: Path,
                         fileToUpload: FileToUpload,
                         featuresDir: JavaPath): Attachment {
        val escapedPath = featurePath.escape()
        val attachmentsDir = featuresDir.resolve(escapedPath.directories.joinToString(separator = "/"))
                .resolve(ATTACHMENTS_DIR)

        val fileName = fileToUpload.getOrCreateOriginalFileName()
        val filePath = attachmentsDir.resolve(fileName)

        filePath.parent?.createDirectories()

        Files.copy(
                fileToUpload.inputStream,
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        )

        return fileToAttachment(filePath, featuresDir)
    }

    private fun fileToAttachment(file: JavaPath, featuresDir: JavaPath): Attachment {
        val attributes = file.getBasicFileAttributes()

        return Attachment(
                path = Path.createInstance(featuresDir.relativize(file).toString()),
                mimeType = Files.probeContentType(file),
                size = attributes.size(),
                lastModifiedDate = LocalDateTime.ofInstant(
                        attributes.lastModifiedTime().toInstant(),
                        ZoneId.systemDefault()
                )
        )
    }

    private fun FileToUpload.getOrCreateOriginalFileName(): String {
        val originalFileName = this.originalFileName
        if (originalFileName == null) {
            // some browsers don't send the original file name with the MultiPart upload
            return UUID.randomUUID().toString()
        }

        // some browsers may send an full path: take only the file name
        val fullFileName = originalFileName.substringAfterLast('/')

        // creating a Path, to make sure we don't escape the dot "." between the filename and the extension
        return Path.createInstance(fullFileName)
                .escape()
                .toString()
    }

}
