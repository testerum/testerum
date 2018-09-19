package com.testerum.web_backend.services.features

import com.testerum.common_jdk.resizeImage
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.file.FeatureFileService
import com.testerum.model.feature.Feature
import com.testerum.model.feature.filter.FeaturesTreeFilter
import com.testerum.model.feature.tree.RootFeatureNode
import com.testerum.model.feature.tree.builder.FeatureTreeBuilder
import com.testerum.model.file.Attachment
import com.testerum.model.file.FileToUpload
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.test.TestModel
import com.testerum.web_backend.services.dirs.FrontendDirs
import com.testerum.web_backend.services.features.filterer.FeaturesTreeFilterer
import org.apache.commons.io.IOUtils
import javax.imageio.ImageIO
import javax.servlet.http.HttpServletResponse
import java.nio.file.Path as JavaPath

class FeaturesFrontendService(private val frontendDirs: FrontendDirs,
                              private val featuresCache: FeaturesCache,
                              private val testsCache: TestsCache,
                              private val featureFileService: FeatureFileService) {

    companion object {
        private val ATTACHMENT_THUMBNAIL_WIDTH = 200
        private val ATTACHMENT_THUMBNAIL_HEIGHT = 150
    }

    /**
     * @return all the features known to the system, without attachments.
     */
    fun getAllFeatures(): Collection<Feature> {
        return featuresCache.getAllFeatures()
                .sortedBy { it.path.toString() }
    }

    /**
     * @return the features tree, containing features and test information, filtered with the given filter
     */
    fun getFeaturesTree(filter: FeaturesTreeFilter): RootFeatureNode {
        val rootNodeBuilder = FeatureTreeBuilder()

        // features
        val features: Collection<Feature> = getAllFeatures()
        val filteredFeatures: List<Feature> = FeaturesTreeFilterer.filterFeatures(features, filter)
        filteredFeatures.forEach {
            rootNodeBuilder.addFeature(it)
        }

        // tests
        val tests: Collection<TestModel> = testsCache.getAllTests()
        val filteredTests: List<TestModel> = FeaturesTreeFilterer.filterTests(tests, filter)
        filteredTests.forEach {
            rootNodeBuilder.addTest(it)
        }

        return rootNodeBuilder.build()
    }

    /**
     * @return the feature at the given path, including attachments.
     */
    fun getFeatureAtPath(path: Path): Feature? {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        val feature = featuresCache.getFeatureAtPath(path)
                ?: featureFileService.createVirtualFeature(path)

        val attachments = featureFileService.getAttachmentsAtPath(path, featuresDir)

        return feature.copy(attachments = attachments)
    }

    /**
     * Creates, renames, and/or updates a feature.
     *
     * Decisions of whether this is a create, rename, etc. are taken comparing ``path`` with ``oldPath``.
     *
     * If the feature was renamed, the tests cache will be updated: all the tests inside this feature
     * will be changed to have the new path.
     *
     * Note that the given feature's ``attachments`` field is ignored.
     * To update/delete the attachments, other methods needs to be used.
     */
    fun save(feature: Feature): Feature {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        val savedFeature = featuresCache.save(feature, featuresDir)

        // if the feature was renamed, notify tests cache
        val oldPath = feature.oldPath
        val newPath = savedFeature.path
        if (oldPath != null && newPath != oldPath) {
            testsCache.directoryWasRenamed(oldPath, newPath)
        }

        return savedFeature
    }

    /**
     * Deletes the feature at the given path.
     *
     * The files deleted include:
     * - the feature file
     * - the feature's attachments
     * - the feature's sub-features and tests, recursively
     */
    fun delete(path: Path) {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        featuresCache.deleteFeatureAndAttachments(path, featuresDir)
        testsCache.featureWasDeleted()
    }

    fun uploadAttachments(featurePath: Path, filesToUpload: List<FileToUpload>): List<Attachment> {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        val result = mutableListOf<Attachment>()

        for (fileToUpload in filesToUpload) {
            result += featureFileService.uploadAttachment(featurePath, fileToUpload, featuresDir)
        }

        return result
    }

    fun writeAttachmentFileContentToResponse(attachmentFilePath: Path,
                                             thumbnail: Boolean,
                                             response: HttpServletResponse) {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        val attachment = featureFileService.getAttachmentAtPath(attachmentFilePath, featuresDir)
                ?: return

        response.reset()
        response.bufferSize = DEFAULT_BUFFER_SIZE

        if (thumbnail && attachment.mimeType?.startsWith("image/") == true) {
            writeImageThumbnailToResponse(attachmentFilePath, featuresDir, attachment, response)
        } else {
            writeFullAttachmentFileContentToContent(attachmentFilePath, featuresDir, attachment, response)
        }
    }

    private fun writeImageThumbnailToResponse(attachmentFilePath: Path,
                                              featuresDir: JavaPath,
                                              attachment: Attachment,
                                              response: HttpServletResponse) {
        val attachmentInputStream = featureFileService.openAttachmentContentInputStream(attachmentFilePath, featuresDir)
                ?: return

        attachmentInputStream.use {
            // headers
            if (attachment.mimeType != null) {
                // will send the thumbnail image as JPEG
                response.setHeader("Content-Type", "image/jpeg")
            }

            // body
            val sourceImage = ImageIO.read(attachmentInputStream)
            val resizedImage = resizeImage(sourceImage, ATTACHMENT_THUMBNAIL_WIDTH, ATTACHMENT_THUMBNAIL_HEIGHT)

            ImageIO.write(resizedImage, "jpg", response.outputStream)
        }
    }

    private fun writeFullAttachmentFileContentToContent(attachmentFilePath: Path,
                                                        featuresDir: JavaPath,
                                                        attachment: Attachment,
                                                        response: HttpServletResponse) {
        val attachmentInputStream = featureFileService.openAttachmentContentInputStream(attachmentFilePath, featuresDir)
                ?: return

        attachmentInputStream.use {
            // headers
            if (attachment.mimeType != null) {
                response.setHeader("Content-Type", attachment.mimeType)
            }
            response.setHeader("Content-Disposition", "inline; filename=${attachment.path.fullFilename}")

            // body
            IOUtils.copy(attachmentInputStream, response.outputStream)
        }
    }

    fun deleteAttachment(attachmentFilePath: Path) {
        val featuresDir = frontendDirs.getRequiredFeaturesDir()

        featureFileService.deleteAttachment(attachmentFilePath, featuresDir)
    }

}
