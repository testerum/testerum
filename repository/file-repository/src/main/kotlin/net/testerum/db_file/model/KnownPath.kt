package net.testerum.db_file.model

import net.qutester.model.infrastructure.path.Path
import net.qutester.model.repository.enums.FileType

data class KnownPath(val directories: List<String>,
                     val fileName: String?,
                     val fileExtension: String?,
                     val fileType: FileType) {

    constructor(pathAsString: String, fileType: FileType): this(Path.createInstance(pathAsString), fileType)

    constructor(path: Path, fileType: FileType): this(path.directories, path.fileName, path.fileExtension, fileType)

    fun asPath(): Path {
        return Path(directories, fileName, fileExtension)
    }

    fun isFile(): Boolean {
        return fileName != null || fileExtension != null
    }

    override fun toString(): String {
        return Path(directories, fileName, fileExtension).toString()
    }

}