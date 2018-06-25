package net.qutester.service.manual

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.qutester.common.json.ObjectMapperFactory
import net.qutester.model.infrastructure.path.CopyPath
import net.qutester.model.infrastructure.path.Path
import net.qutester.model.infrastructure.path.RenamePath
import net.qutester.model.manual.ManualTest
import net.qutester.model.repository.enums.FileType
import net.qutester.model.manual.operation.UpdateManualTestModel
import net.testerum.db_file.FileRepositoryService
import net.testerum.db_file.model.KnownPath
import net.testerum.db_file.model.RepositoryFile
import net.testerum.db_file.model.RepositoryFileChange


class ManualTestsService(private val fileRepositoryService: FileRepositoryService) {

    val objectMapper: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()

    fun createTest(manualTest: ManualTest): ManualTest {

        val testPath = Path(manualTest.path.directories, manualTest.text, FileType.MANUAL_TEST.fileExtension)

        val fileTestAsString = objectMapper.writeValueAsString(manualTest)

        val createdRepositoryFile = fileRepositoryService.create(
                RepositoryFileChange(
                        null,
                        RepositoryFile(
                                KnownPath(testPath, FileType.MANUAL_TEST),
                                fileTestAsString
                        )
                )
        )

        val resolvedTestModel = manualTest.copy(
                path = createdRepositoryFile.knownPath.asPath()
        )

        return resolvedTestModel
    }

    fun updateTest(updateManualTestModel: UpdateManualTestModel): ManualTest {
        val manualTest = updateManualTestModel.manualTest

        val oldPath = updateManualTestModel.oldPath;
        val newPath = Path(manualTest.path.directories, manualTest.text, FileType.MANUAL_TEST.fileExtension)

        val fileTestAsString = objectMapper.writeValueAsString(manualTest)

        fileRepositoryService.update(
                RepositoryFileChange(
                        KnownPath(oldPath, FileType.MANUAL_TEST),
                        RepositoryFile(
                                KnownPath(newPath, FileType.MANUAL_TEST),
                                fileTestAsString
                        )
                )
        )

        val resolvedTestModel = manualTest.copy(
                path = newPath
        )

        return resolvedTestModel
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.MANUAL_TEST))
    }

    fun getAllTests(): List<ManualTest> {

        val manualTests = mutableListOf<ManualTest>()

        val allTestFiles = fileRepositoryService.getAllResourcesByType(FileType.MANUAL_TEST)
        for (testFile in allTestFiles) {
            val manualTest = objectMapper.readValue<ManualTest>(testFile.body)

            val resolvedManualTest = manualTest.copy(path = testFile.knownPath.asPath())
            manualTests.add(resolvedManualTest)
        }

        return manualTests
    }

    fun getTestAtPath(path: Path): ManualTest? {
        val testFile = fileRepositoryService.getByPath(
                KnownPath(path, FileType.MANUAL_TEST)
        ) ?: return null

        val manualTest = objectMapper.readValue<ManualTest>(testFile.body)
        val resolvedManualTest = manualTest.copy(path = testFile.knownPath.asPath())

        return resolvedManualTest
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        return fileRepositoryService.renameDirectory(
                KnownPath(renamePath.path,FileType.MANUAL_TEST),
                renamePath.newName
        )
    }

    fun deleteDirectory(path: Path) {
        return fileRepositoryService.delete(
                KnownPath(path, FileType.TEST)
        )
    }

    fun moveDirectoryOrFile(copyPath: CopyPath) {
        fileRepositoryService.moveDirectoryOrFile(
                KnownPath(copyPath.copyPath, FileType.TEST),
                KnownPath(copyPath.destinationPath, FileType.TEST)
        )
    }
}