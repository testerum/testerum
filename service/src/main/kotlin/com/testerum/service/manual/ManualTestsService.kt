package com.testerum.service.manual

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_json.ObjectMapperFactory
import com.testerum.service.file_repository.FileRepositoryService
import com.testerum.service.file_repository.model.KnownPath
import com.testerum.service.file_repository.model.RepositoryFile
import com.testerum.service.file_repository.model.RepositoryFileChange
import com.testerum.model.infrastructure.path.CopyPath
import com.testerum.model.infrastructure.path.Path
import com.testerum.model.infrastructure.path.RenamePath
import com.testerum.model.manual.OldManualTest
import com.testerum.model.repository.enums.FileType


class ManualTestsService(private val fileRepositoryService: FileRepositoryService) {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = ObjectMapperFactory.createKotlinObjectMapper()
    }

    fun save(oldManualTest: OldManualTest): OldManualTest {
        val newPath = Path(oldManualTest.path.directories, oldManualTest.text, FileType.MANUAL_TEST.fileExtension)
        val oldKnownPath = oldManualTest.oldPath?.let { KnownPath(it, FileType.MANUAL_TEST) }

        val fileTestAsString = OBJECT_MAPPER.writeValueAsString(oldManualTest)

        val repositoryFile = fileRepositoryService.save(
                RepositoryFileChange(
                        oldKnownPath,
                        RepositoryFile(
                                KnownPath(newPath, FileType.MANUAL_TEST),
                                fileTestAsString
                        )
                )
        )

        return oldManualTest.copy(
                path = repositoryFile.knownPath.asPath()
        )
    }

    fun remove(path: Path) {
        fileRepositoryService.delete(knownPath = KnownPath(path, FileType.MANUAL_TEST))
    }

    fun getAllTests(): List<OldManualTest> {

        val manualTests = mutableListOf<OldManualTest>()

        val allTestFiles = fileRepositoryService.getAllResourcesByType(FileType.MANUAL_TEST)
        for (testFile in allTestFiles) {
            val manualTest = OBJECT_MAPPER.readValue<OldManualTest>(testFile.body)

            val resolvedManualTest = manualTest.copy(path = testFile.knownPath.asPath())
            manualTests.add(resolvedManualTest)
        }

        return manualTests
    }

    fun getTestAtPath(path: Path): OldManualTest? {
        val testFile = fileRepositoryService.getByPath(
                KnownPath(path, FileType.MANUAL_TEST)
        ) ?: return null

        val manualTest = OBJECT_MAPPER.readValue<OldManualTest>(testFile.body)

        return manualTest.copy(
                path = testFile.knownPath.asPath()
        )
    }

    fun renameDirectory(renamePath: RenamePath): Path {
        return fileRepositoryService.renameDirectory(
                KnownPath(renamePath.path, FileType.MANUAL_TEST),
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
