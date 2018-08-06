package net.qutester.service.config

import com.testerum.model.config.dir_tree.FileSystemDirectory
import com.testerum.model.infrastructure.path.Path
import java.io.File

class FileSystemService {

    fun getDirectoryTree(path: Path): FileSystemDirectory {
        val childDirectories: List<FileSystemDirectory> = if (path.isEmpty()) {
            File.listRoots().map {
                FileSystemDirectory(
                        Path(listOf(it.absolutePath), null, null),
                        it.listFiles(File::isDirectory) != null
                )
            }
        } else {
            path.toJavaPath()
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
                path,
                childDirectories.isNotEmpty(),
                childDirectories
        )
    }

    private fun hasSubDirectories(directory: File): Boolean {
        val subFiles = directory.listFiles(File::isDirectory) ?: return false

        return subFiles.any()
    }
}
