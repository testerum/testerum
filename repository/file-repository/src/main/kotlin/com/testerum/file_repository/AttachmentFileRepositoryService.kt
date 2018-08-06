package com.testerum.file_repository

import com.testerum.file_repository.model.KnownPath
import net.qutester.model.file.Attachment
import net.qutester.model.infrastructure.path.Path
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.nio.file.attribute.BasicFileAttributeView
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.stream.Collectors.toList


class AttachmentFileRepositoryService(private val fileRepositoryService: FileRepositoryService) {

    companion object {
        private const val ATTACHMENTS_DIR = "_attachments"
    }

    fun uploadFiles(entityPath: KnownPath, uploadFiles: Array<MultipartFile>): List<Attachment> {
        val uploadedFilePaths = mutableListOf<Attachment>()

        for (uploadFile in uploadFiles) {
            uploadedFilePaths.add(
                    uploadFile(entityPath, uploadFile)
            )
        }

        return uploadedFilePaths
    }

    private fun uploadFile(entityPath: KnownPath, uploadFile: MultipartFile): Attachment {

        val fileNameToUploadAsPath = Path.createInstance(uploadFile.originalFilename)
        val fileToUploadKnownPath = KnownPath(
                entityPath.directories + ATTACHMENTS_DIR,
                fileNameToUploadAsPath.fileName,
                fileNameToUploadAsPath.fileExtension,
                entityPath.fileType
        )

        val escapedRelativeKnownPath = fileRepositoryService.escapeIllegalCharactersInPath(fileToUploadKnownPath)
        val absoluteResourcePath = fileRepositoryService.getAbsoluteUniquePath(escapedRelativeKnownPath)

        fileRepositoryService.createParentDirectoryIfNotExisting(absoluteResourcePath)

        Files.write(
                absoluteResourcePath,
                uploadFile.bytes,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
        )

        return getAttachmentDetailsFromPath(escapedRelativeKnownPath)!!
    }

    fun getAttachmentDetailsFromPath(knownPath: KnownPath): Attachment? {
        val escapedRelativeKnownPath = fileRepositoryService.escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = fileRepositoryService.escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            return null
        }

        val mimeType = Files.probeContentType(absoluteResourcePath)

        val fileBasicView = Files.getFileAttributeView(absoluteResourcePath, BasicFileAttributeView::class.java)
        val fileAttributes = fileBasicView.readAttributes()
        return Attachment (
                knownPath.asPath(),
                mimeType,
                fileAttributes.size(),
                LocalDateTime.ofInstant(fileAttributes.lastModifiedTime().toInstant(), ZoneId.systemDefault())
        )
    }

    fun getAttachment(knownPath: KnownPath): ByteArray? {
        val escapedRelativeKnownPath = fileRepositoryService.escapeIllegalCharactersInPath(knownPath)
        val absoluteResourcePath: java.nio.file.Path = fileRepositoryService.escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            return null
        }

        return absoluteResourcePath.toFile().readBytes()
    }

    fun getAttachmentsDetailsFromPath(knownPath: KnownPath): List<Attachment> {
        val fileToUploadKnownPath = KnownPath(
                knownPath.directories + ATTACHMENTS_DIR,
                null,
                null,
                knownPath.fileType
        )

        val escapedRelativeKnownPath = fileRepositoryService.escapeIllegalCharactersInPath(fileToUploadKnownPath)
        val absoluteResourcePath = fileRepositoryService.escapeAndGetAbsolutePath(escapedRelativeKnownPath)

        if (!absoluteResourcePath.toFile().exists()) {
            return emptyList()
        }
        val attachmentsFiles = Files.list(absoluteResourcePath)
                .filter { it.toFile().isFile }
                .collect(toList())

        val result = mutableListOf<Attachment>()
        for (attachmentsFile in attachmentsFiles) {
            val relativeJavaPathAsString = fileRepositoryService.getRelativePathFromAbsolutePath(attachmentsFile.toAbsolutePath(), knownPath.fileType).toString()
            val path = Path.createInstance(relativeJavaPathAsString)

            val attachment = getAttachmentDetailsFromPath(KnownPath(path, knownPath.fileType))
            result.add(
                    attachment!!
            )
        }

        return result
    }

    fun delete(knownPath: KnownPath) {
        fileRepositoryService.delete(knownPath)
    }
}