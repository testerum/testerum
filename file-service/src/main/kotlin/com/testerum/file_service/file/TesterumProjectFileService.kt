package com.testerum.file_service.file

import com.testerum.common_kotlin.doesNotExist
import com.testerum.common_kotlin.exists
import com.testerum.common_kotlin.isNotADirectory
import java.nio.file.Path as JavaPath

class TesterumProjectFileService {

    fun isTesterumProject(directory: JavaPath): Boolean {
        if (directory.doesNotExist) {
            return false
        }
        if (directory.isNotADirectory) {
            return false
        }

        val projectFile = projectFilePath(directory)

        return projectFile.exists
    }

    private fun projectFilePath(directory: JavaPath): JavaPath {
        return directory.resolve("Testerum-Project.yaml")
    }

}
