package net.qutester.service.feature

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.qutester.common.json.ObjectMapperFactory
import net.qutester.exception.ValidationException
import net.qutester.model.feature.Feature
import net.qutester.model.file.Attachment
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType
import net.qutester.model.test.TestModel
import net.qutester.model.tree.*
import net.qutester.service.tests.TestsService
import net.testerum.db_file.AttachmentFileRepositoryService
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange
import org.springframework.web.multipart.MultipartFile


class FeatureService(private val fileRepositoryService: FileRepositoryService,
                     private val attachmentFileRepositoryService: AttachmentFileRepositoryService,
                     private val testsService: TestsService) {

    val objectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()

    val FEATURE_NAME: String = "info";

    fun createFeature(feature: Feature): Feature {

        val featurePath = Path(feature.path.directories, FEATURE_NAME, FileType.FEATURE.fileExtension)

        val fileTestAsString = objectMapper.writeValueAsString(feature)

        val createdRepositoryFile = fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(featurePath, FileType.FEATURE),
                                fileTestAsString
                        )
                )
        )

        val savedFeature = feature.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )

        return savedFeature
    }

    fun updateFeature(feature: Feature): Feature {

        try {
            return updateFeatureWithoutErrorHandling(feature)
        } catch (e: FileAlreadyExistsException) {
            throw ValidationException().addFiledValidationError(
                    "name",
                    "a_resource_with_the_same_name_already_exist"
            )
        } catch (e: java.nio.file.AccessDeniedException) {
            throw ValidationException().addFiledValidationError(
                    "name",
                    "access_denied"
            )
        } catch (e: Exception) {
            throw ValidationException().addFiledValidationError(
                    "name",
                    "access_denied"
            )
        }
    }

    private fun updateFeatureWithoutErrorHandling(feature: Feature): Feature {
        val oldPath = feature.path;
        var newPath = oldPath;

        val oldName = if (oldPath.directories.isEmpty()) null else oldPath.directories.last()
        if (oldName != null && oldName != feature.name) {
            val newDirectoryPath = fileRepositoryService.renameDirectory(
                    KnownPath(Path(oldPath.directories, null, null), FileType.FEATURE),
                    feature.name
            )
//            newPath = newDirectoryPath;
//TODO Ionut: remove top uncoment bottom
            newPath = newDirectoryPath.copy(
                    fileName = oldPath.fileName,
                    fileExtension = oldPath.fileExtension
            )
        }

        val fileFeatureAsString = objectMapper.writeValueAsString(
                feature.copy(path = newPath)
        )

        fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(newPath, FileType.FEATURE),
                        RepositoryFile(
                                KnownPath(newPath, FileType.FEATURE),
                                fileFeatureAsString
                        )
                )
        )

        val savedFeature = feature.copy(
                path = newPath
        )

        return savedFeature
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.FEATURE))
    }

    fun getAllFeatures(): List<Feature> {

        val features = mutableListOf<Feature>()

        val allFeatureFiles = fileRepositoryService.getAllResourcesByType(FileType.FEATURE)
        for (featureFile in allFeatureFiles) {
            val feature = objectMapper.readValue<Feature>(featureFile.body)

            val resolvedFeature = feature.copy(path = featureFile.knownPath.asPath())
            features.add(resolvedFeature)
        }

        return features
    }

    fun getFeatureAtPath(path: Path): Feature? {
        val filePath = path.copy(
                fileName = FEATURE_NAME,
                fileExtension = FileType.FEATURE.fileExtension
        );

        val featureKnownPath = KnownPath(filePath, FileType.FEATURE)
        val featureFile = fileRepositoryService.getByPath(
                featureKnownPath
        ) ?: return null

        val feature = objectMapper.readValue<Feature>(featureFile.body)

        val attachmentsDetailsFromPath = attachmentFileRepositoryService.getAttachmentsDetailsFromPath(featureKnownPath)

        return feature.copy(
                path = featureFile.knownPath.asPath(),
                attachments = attachmentsDetailsFromPath
        )
    }

    fun uploadFiles(featurePath: Path, uploadingFiles: Array<MultipartFile>): List<Attachment> {
        return attachmentFileRepositoryService.uploadFiles(
                KnownPath(featurePath, FileType.FEATURE),
                uploadingFiles
        );
    }

    fun getFeaturesTree(): RootTreeNode {
        val rootNode = RootTreeNode("Features");

        val features = getAllFeatures()
        mapFeaturesToTree(features, rootNode)

        val tests = testsService.getAllTests()
        mapTestsToTree(tests, rootNode)

        return rootNode;
    }

    private fun mapFeaturesToTree(features: List<Feature>, rootNode: RootTreeNode) {
        for (feature in features) {
            val featureDirs = feature.path.directories.toMutableList()
            findOrCreateFeatureTreeNodes(featureDirs, rootNode)
        }
    }

    private fun findOrCreateFeatureTreeNodes(featuresNames: MutableList<String>, parentNode: ContainerTreeNode): TreeNode {
        if (featuresNames.isEmpty()) {
            return parentNode;
        }

        val currentFeatureName = featuresNames.removeAt(0)

        var treeNode: ContainerTreeNode? = parentNode.children.find { it is ContainerTreeNode && it.name == currentFeatureName } as? ContainerTreeNode
        if (treeNode == null) {
            treeNode = FeatureTreeNode(
                    currentFeatureName,
                    parentNode.path.copy(directories = parentNode.path.directories + currentFeatureName)
            )
            parentNode.children.add(treeNode)
        }

        return findOrCreateFeatureTreeNodes(
                featuresNames,
                treeNode
        )
    }

    private fun mapTestsToTree(tests: List<TestModel>, rootNode: RootTreeNode) {
        for (test in tests) {
            val parentContainerTreeNode = findOrCreateFeatureTreeNodes(
                    test.path.directories.toMutableList(),
                    rootNode
            ) as ContainerTreeNode

            parentContainerTreeNode.children.add(
                    TestTreeNode(
                            test.text,
                            test.path
                    )
            )
        }
    }
}