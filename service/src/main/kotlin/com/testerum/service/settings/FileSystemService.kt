package com.testerum.service.settings

import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.infrastructure.path.Path
import java.io.File
import java.nio.file.Paths

class FileSystemService {

    fun getDirectoryTree(pathAsString: String): FileSystemDirectory {
        val childDirectories: List<FileSystemDirectory> = if (pathAsString == "") {
            File.listRoots().map {
                FileSystemDirectory(
                        Path(listOf(it.absolutePath), null, null),
                        it.listFiles(File::isDirectory) != null
                )
            }
        } else {
            Paths.get(pathAsString)
                    .toFile()
                    .listFiles(File::isDirectory)
                    .sortedBy { it.toPath().toString() }
                    .map {
                        FileSystemDirectory(
                                Path(it.absolutePath.split(File.separator), null, null),
                                hasSubDirectories(it)
                        )
                    }
        }

        return FileSystemDirectory(
                Path.createInstance(pathAsString),
                childDirectories.isNotEmpty(),
                childDirectories
        )
    }

    private fun hasSubDirectories(directory: File): Boolean {
        val subFiles = directory.listFiles(File::isDirectory) ?: return false

        return subFiles.any()
    }
}
