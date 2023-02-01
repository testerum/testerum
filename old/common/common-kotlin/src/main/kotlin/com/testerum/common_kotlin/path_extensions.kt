@file:Suppress("unused")

package com.testerum.common_kotlin

import org.apache.commons.io.FileUtils
import java.io.IOException
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.NoSuchFileException
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.util.UUID
import java.util.stream.Collectors
import java.nio.file.Path as JavaPath

val JavaPath.exists: Boolean
    get() = Files.exists(this)

val JavaPath.doesNotExist: Boolean
    get() = !exists

val JavaPath.isRegularFile: Boolean
    get() = Files.isRegularFile(this)

val JavaPath.isNotARegularFile: Boolean
    get() = !isRegularFile

val JavaPath.isDirectory: Boolean
    get() = Files.isDirectory(this)

val JavaPath.isNotADirectory: Boolean
    get() = !isDirectory

val JavaPath.isExecutable: Boolean
    get() = Files.isExecutable(this)

val JavaPath.isNotExecutable: Boolean
    get() = !isExecutable

val JavaPath.isHidden: Boolean
    get() = Files.isHidden(this)

val JavaPath.isNotHidden: Boolean
    get() = !isHidden

val JavaPath.isReadable: Boolean
    get() = Files.isReadable(this)

val JavaPath.isNotReadable: Boolean
    get() = !isReadable

val JavaPath.isWritable: Boolean
    get() = Files.isWritable(this)

val JavaPath.isNotWritable: Boolean
    get() = !isWritable

val JavaPath.isSymbolicLink: Boolean
    get() = Files.isSymbolicLink(this)

val JavaPath.isNotASymbolicLink: Boolean
    get() = !isSymbolicLink

val JavaPath.hasChildren: Boolean
    get() {
        if (isNotADirectory) {
            return false
        }

        return Files.list(this).use { pathStream ->
            pathStream.isNotEmpty()
        }
    }

val JavaPath.canCreateChild: Boolean
    get() = isDirectory && isWritable && isExecutable // if the directory is not executable, we won't be able to create files or directories inside it

val JavaPath.hasSubDirectories: Boolean
    get() {
        if (!Files.isDirectory(this)) {
            return false
        }

        try {
            Files.list(this).use { pathStream ->
                return pathStream.anyMatch { path ->
                    path.isDirectory
                }
            }
        } catch (e: Exception) {
            return false
        }
    }

val JavaPath.hasNoChildren: Boolean
    get() = !hasChildren

fun JavaPath.isSameFileAs(other: JavaPath): Boolean {
    return Files.isSameFile(this, other)
}

fun JavaPath.isNotSameFileAs(other: JavaPath): Boolean = !this.isSameFileAs(other)

fun JavaPath.list(): List<JavaPath> {
    if (this.doesNotExist) {
        return emptyList()
    }

    Files.list(this).use { pathStream ->
        return pathStream.collect(
                Collectors.toList()
        )
    }
}

fun JavaPath.list(shouldUse: (JavaPath) -> Boolean): List<JavaPath> {
    if (this.doesNotExist) {
        return emptyList()
    }

    Files.list(this).use { pathStream ->
        return pathStream.filter(shouldUse)
                .collect(Collectors.toList())
    }
}

/**
 * Moves this file to the path represented by ``destination``.
 *
 * If ``destination`` is the same file as ``this`` (but for example, with a different case, on case-insensitive file systems),
 * the file will be renamed.
 *
 * Throws an exception if ``destination`` exists and it's a different file from ``this``.
 */
fun JavaPath.smartMoveTo(destination: JavaPath,
                         createDestinationExistsException: () -> Exception) {
    if (this.doesNotExist) {
        return
    }

    if (destination.doesNotExist) {
        destination.parent?.createDirectories()
        Files.move(this, destination)
    } else {
        val isSameFile = this.isSameFileAs(destination)

        if (isSameFile) {
            if (this.toAbsolutePath().toString() != destination.toAbsolutePath().toString()) {
                // this can happen for example when changing letter casing, on a case-insensitive file system

                destination.parent?.createDirectories()

                val tempFile = destination.resolveSibling(UUID.randomUUID().toString())

                Files.move(this, tempFile)
                Files.move(tempFile, destination)
            }
        } else {
            throw createDestinationExistsException()
        }
    }
}

/**
 * Copy this file or directory to the path represented by ``destination``.
 *
 * If ``destination`` is the same file as ``this`` (but for example, with a different case, on case-insensitive file systems),
 * the file will be renamed.
 *
 * Throws an exception if ``destination`` exists and it's a different file from ``this``.
 */
fun JavaPath.smartCopyTo(destination: JavaPath,
                         createDestinationExistsException: () -> Exception) {
    if (this.doesNotExist) {
        return
    }

    if (destination.doesNotExist) {
        if (this.isDirectory) {
            FileUtils.copyDirectory(this.toFile(), destination.toFile())
        } else {
            destination.parent?.createDirectories()
            Files.copy(this, destination)
        }
    } else {
        throw createDestinationExistsException()
    }
}

fun JavaPath.readLines(charset: Charset = Charsets.UTF_8): List<String> = Files.readAllLines(this, charset)

fun JavaPath.readText(charset: Charset = Charsets.UTF_8): String {
    return Files.newBufferedReader(this, charset).use {
        it.readText()
    }
}

fun JavaPath.writeText(text: String, charset: Charset = Charsets.UTF_8) {
    this.parent?.createDirectories()

    Files.newBufferedWriter(this, charset).use {
        it.write(text)
    }
}

fun JavaPath.writeLines(lines: List<String>, charset: Charset = Charsets.UTF_8) {
    writeText(
            text = lines.joinToString(separator = "\n"),
            charset = charset
    )
}

fun JavaPath.deleteIfExists(): Boolean = Files.deleteIfExists(this)
fun JavaPath.delete(): Unit = Files.delete(this)
fun JavaPath.deleteRecursivelyIfExists() {
    if (this.doesNotExist) {
        return
    }
    if (!this.isDirectory) {
        deleteIfExists()
    }

    walkFileTree(object : SimpleFileVisitor<JavaPath>() {
        override fun visitFile(file: JavaPath, attrs: BasicFileAttributes): FileVisitResult {
            file.delete()

            return FileVisitResult.CONTINUE
        }

        override fun postVisitDirectory(dir: JavaPath, exception: IOException?): FileVisitResult {
            if (exception != null) {
                throw exception
            }

            dir.delete()

            return FileVisitResult.CONTINUE
        }
    })
}

fun JavaPath.deleteContentsRecursivelyIfExists() {
    if (this.doesNotExist) {
        return
    }
    if (!this.isDirectory) {
        deleteIfExists()
    }

    for (fileOrDir in list()) {
        fileOrDir.deleteRecursivelyIfExists()
    }
}

fun JavaPath.createDirectories(vararg attrs: FileAttribute<*>): JavaPath = Files.createDirectories(this, *attrs)
fun JavaPath.createDirectory(vararg attrs: FileAttribute<*>): JavaPath = Files.createDirectory(this, *attrs)

fun JavaPath.hasExtension(extension: String) = toString().endsWith(extension)
fun JavaPath.getContent(charset: Charset = Charsets.UTF_8): String = String(Files.readAllBytes(this), charset)
fun JavaPath.getContentOrNull(charset: Charset = Charsets.UTF_8): String? {
    if (!this.exists) {
        // avoid exception if the file doesn't exist
        return null
    }

    return try {
        String(Files.readAllBytes(this), charset)
    } catch (e: NoSuchFileException) {
        // catch exception in the unlikely event that the file is deleted between the check above and 
        null
    }
}

fun <V : FileAttributeView> JavaPath.getFileAttributeView(type: Class<V>): V = Files.getFileAttributeView(this, type)

fun JavaPath.getBasicFileAttributeView(): BasicFileAttributeView = getFileAttributeView(BasicFileAttributeView::class.java)
fun JavaPath.getBasicFileAttributes(): BasicFileAttributes = getBasicFileAttributeView().readAttributes()

fun JavaPath.walkFileTree(visitor: FileVisitor<JavaPath>) {
    if (this.doesNotExist) {
        return
    }
    if (!this.isDirectory) {
        deleteIfExists()
    }

    Files.walkFileTree(this, visitor)
}

/**
 * Traverse this directory recursively, passing each path (file or directory) to the given lambda.
 */
inline fun JavaPath.walk(crossinline handlePath: (JavaPath) -> Unit) {
    if (this.doesNotExist) {
        return
    }

    Files.walk(this).use { pathStream ->
        pathStream.forEach { path ->
            handlePath(path)
        }
    }
}

inline fun JavaPath.walkAndCollect(crossinline shouldUse: (JavaPath) -> Boolean): List<JavaPath> {
    val results = mutableListOf<JavaPath>()

    walk {
        if (shouldUse(it)) {
            results.add(it)
        }
    }

    return results
}

fun JavaPath.deleteOnExit() = this.toFile().deleteOnExit()

fun JavaPath.canonicalize(): JavaPath = toAbsolutePath().normalize()

fun List<JavaPath>.toUrlArray(): Array<URL> {
    val result = ArrayList<URL>()

    for (path in this) {
        if (path.doesNotExist) {
            continue
        }
        result += path.toUri().toURL()
    }

    // not using list.toTypedArray()" because it's inneficient:
    // it allocates a zero-sized array first, and then
    // the java method will allocate another array with the proper size
    @Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN", "UNCHECKED_CAST")
    return (result as java.util.List<URL>).toArray(
        arrayOfNulls(result.size)
    )
}
