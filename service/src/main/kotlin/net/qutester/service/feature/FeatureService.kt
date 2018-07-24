package net.qutester.service.feature

import com.testerum.common.parsing.executer.ParserExecuter
import com.testerum.common.string.containsSearchStringParts
import com.testerum.test_file_format.feature.FileFeature
import com.testerum.test_file_format.feature.FileFeatureParserFactory
import com.testerum.test_file_format.feature.FileFeatureSerializer
import net.qutester.exception.ValidationException
import net.qutester.model.feature.Feature
import net.qutester.model.feature.filter.FeaturesTreeFilter
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

    companion object {
        private val FEATURE_NAME: String = "info"

        private val FILE_PARSER: ParserExecuter<FileFeature> = ParserExecuter(
                FileFeatureParserFactory.feature()
        )
    }

    fun save(feature: Feature): Feature {
        try {
            return saveFeatureWithoutErrorHandling(feature)
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
        }
    }

    private fun saveFeatureWithoutErrorHandling(feature: Feature): Feature {
        val featureDirKnownPath = KnownPath(
                Path(feature.path.directories, null, null),
                FileType.FEATURE
        )

        if (!fileRepositoryService.existResourceAtPath(featureDirKnownPath)) {
            return createFeature(feature)
        }

        return updateFeature(feature)
    }

    private fun createFeature(feature: Feature): Feature {
        val fileTestAsString: String = FileFeatureSerializer.serializeToString(
                FileFeature(
                        description = feature.description,
                        tags = feature.tags
                )
        )
        val featureDirPath = feature.path
        val featureFilePath = featureDirPath.copy(fileName = FEATURE_NAME, fileExtension = FileType.FEATURE.fileExtension)


        val createdRepositoryFile = fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(featureFilePath, FileType.FEATURE),
                                fileTestAsString
                        )
                )
        )

        return feature.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )
    }

    private fun updateFeature(feature: Feature): Feature {
        val newFeatureDirPath = renameDirectoryIfCase(feature)
        val newPath = newFeatureDirPath.copy(fileName = FEATURE_NAME, fileExtension = FileType.FEATURE.fileExtension)
        val newKnownPath = KnownPath(newPath, FileType.FEATURE)

        val featureAsString: String = FileFeatureSerializer.serializeToString(
                FileFeature(
                        description = feature.description,
                        tags = feature.tags
                )
        )
        val newRepositoryFile = RepositoryFile(newKnownPath, featureAsString)

        if (!fileRepositoryService.existResourceAtPath(newKnownPath)) {
            fileRepositoryService.create(
                    RepositoryFileChange(
                            null,
                            newRepositoryFile
                    )
            )
        } else {
            fileRepositoryService.update(
                    RepositoryFileChange(
                            KnownPath(feature.path, FileType.FEATURE),
                            newRepositoryFile
                    )
            )
        }

        val savedFeature = feature.copy(
                path = newPath
        )

        return savedFeature
    }

    private fun renameDirectoryIfCase(feature: Feature): Path {
        val oldDirPath = feature.path.copy(fileName = null, fileExtension = null)

        val oldName = if (oldDirPath.directories.isEmpty()) null else oldDirPath.directories.last()
        if (oldName != null && oldName != feature.name) {
            return fileRepositoryService.renameDirectory(
                    KnownPath(Path(oldDirPath.directories, null, null), FileType.FEATURE),
                    feature.name
            )
        }
        return oldDirPath
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.FEATURE))
    }

    fun getAllFeatures(): List<Feature> {
        val features = mutableListOf<Feature>()

        val allFeatureFiles = fileRepositoryService.getAllResourcesByType(FileType.FEATURE)
        for (featureFile in allFeatureFiles) {
            val fileFeature: FileFeature = FILE_PARSER.parse(featureFile.body)

            val path: Path = featureFile.knownPath.asPath()
            val feature = Feature(
                    path = path,
                    name = getFeatureName(path),
                    description = fileFeature.description,
                    tags = fileFeature.tags
            )

            features.add(feature)
        }

        return features
    }

    fun getFeatureAtPath(path: Path): Feature? {
        val filePath = path.copy(
                fileName = FEATURE_NAME,
                fileExtension = FileType.FEATURE.fileExtension
        )

        val featureKnownPath = KnownPath(filePath, FileType.FEATURE)
        val featureFile = fileRepositoryService.getByPath(featureKnownPath)
                ?: return Feature(filePath, getFeatureName(filePath))

        val fileFeature: FileFeature = FILE_PARSER.parse(featureFile.body)

        val featurePath: Path = featureFile.knownPath.asPath()
        val feature = Feature(
                path = featurePath,
                name = getFeatureName(filePath),
                description = fileFeature.description,
                tags = fileFeature.tags
        )

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
        )
    }

    fun getFeaturesTree(featuresTreeFilter: FeaturesTreeFilter): RootTreeNode {
        val rootNode = RootTreeNode("Features")

        val features = getAllFeatures()
        val filteredFeatures = filterFeatures(featuresTreeFilter, features)
        mapFeaturesToTree(filteredFeatures, rootNode)

        val tests = testsService.getAllTests()
        val filteredTests = filterTests(featuresTreeFilter, tests)
        mapTestsToTree(filteredTests, rootNode)

        return rootNode
    }

    private fun filterFeatures(featuresTreeFilter: FeaturesTreeFilter, features: List<Feature>): List<Feature> {
        val results = mutableListOf<Feature>()
        for (feature in features) {
            val featureIsMatchSearchFilterCriteria = featureMatchesSearchFilterCriteria(feature, featuresTreeFilter)
            val featureIsMatchTagsFilterCriteria = tagListMatchesTagsFilterCriteria(feature.tags, featuresTreeFilter)

            if (featureIsMatchSearchFilterCriteria && featureIsMatchTagsFilterCriteria) {
                results.add(feature)
            }
        }

        return results
    }

    private fun tagListMatchesTagsFilterCriteria(tags: List<String>, featuresTreeFilter: FeaturesTreeFilter): Boolean {
        val featureUpperCasedTags = tags.map(String::toUpperCase)

        return featureUpperCasedTags.containsAll(
                featuresTreeFilter.tags.map(String::toUpperCase)
        )
    }

    private fun featureMatchesSearchFilterCriteria(feature: Feature, featuresTreeFilter: FeaturesTreeFilter): Boolean {
        var featureIsMatchFilter = false

        if (feature.path.toString().containsSearchStringParts(featuresTreeFilter.search)) {
            featureIsMatchFilter = true
        }

        if (feature.name.containsSearchStringParts(featuresTreeFilter.search)) {
            featureIsMatchFilter = true
        }

        if (feature.description.containsSearchStringParts(featuresTreeFilter.search)) {
            featureIsMatchFilter = true
        }
        return featureIsMatchFilter
    }

    private fun filterTests(featuresTreeFilter: FeaturesTreeFilter, tests: List<TestModel>): List<TestModel> {
        val results = mutableListOf<TestModel>()
        for (test in tests) {
            val testMatchesTypeFilter = testMatchesTypeFilter(test, featuresTreeFilter)
            val testMatchesTestFilter = testMatchesSearchFilter(test, featuresTreeFilter)
            val testIsMatchTagsFilterCriteria = tagListMatchesTagsFilterCriteria(test.tags, featuresTreeFilter)

            if (testMatchesTypeFilter && testMatchesTestFilter && testIsMatchTagsFilterCriteria) {
                results.add(test)
            }
        }

        return results
    }

    private fun testMatchesTypeFilter(test: TestModel, featuresTreeFilter: FeaturesTreeFilter): Boolean {
        return test.properties.isManual == featuresTreeFilter.showManualTest ||
                !test.properties.isManual == featuresTreeFilter.showAutomatedTests
    }

    private fun testMatchesSearchFilter(test: TestModel, featuresTreeFilter: FeaturesTreeFilter): Boolean {
        var testMatchesTestFilter = false

        if (test.path.toString().containsSearchStringParts(featuresTreeFilter.search)) {
            testMatchesTestFilter = true
        }

        if (test.text.containsSearchStringParts(featuresTreeFilter.search)) {
            testMatchesTestFilter = true
        }
        return testMatchesTestFilter
    }

    private fun mapFeaturesToTree(features: List<Feature>, rootNode: RootTreeNode) {
        for (feature in features) {
            val featureDirs = feature.path.directories.toMutableList()
            findOrCreateFeatureTreeNodes(featureDirs, rootNode)
        }
    }

    private fun findOrCreateFeatureTreeNodes(featuresNames: MutableList<String>, parentNode: ContainerTreeNode): TreeNode {
        if (featuresNames.isEmpty()) {
            return parentNode
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
                            name = test.text,
                            path = test.path,
                            properties = test.properties
                    )
            )
        }
    }

    private fun getFeatureName(featurePath: Path): String {
        if (featurePath.directories.isEmpty()) {
            return ""
        }
        return featurePath.directories.last()
    }
}