@file:Suppress("unused")

package com.testerum.common_kotlin

import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.AccessDeniedException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitor
import java.nio.file.Files
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributeView
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.FileAttribute
import java.nio.file.attribute.FileAttributeView
import java.util.*
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

val JavaPath.isHidden: Boolean
    get() = Files.isHidden(this)

val JavaPath.isReadable: Boolean
    get() = Files.isReadable(this)

val JavaPath.isWritable: Boolean
    get() = Files.isWritable(this)

val JavaPath.isSymbolicLink: Boolean
    get() = Files.isSymbolicLink(this)

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
        } catch (e: AccessDeniedException) {
            return false
        }
    }

val JavaPath.hasNoChildren: Boolean
    get() = !hasChildren

fun JavaPath.isSameFileAs(other: JavaPath): Boolean {
    return Files.isSameFile(this, other)
}

fun JavaPath.isNotSameFileAs(other: JavaPath): Boolean = !this.isSameFileAs(other)

/**
 * Moves this file to the path represented by ``other``.
 *
 * If ``other`` is the same file as ``this`` (but for example, with a different case, on case-insensitive file systems),
 * the file will be renamed.
 *
 * Throws an exception if ``other`` exists and it's a different file from ``this``.
 */
fun JavaPath.smartMoveTo(other: JavaPath,
                         createDestinationExistsException: () -> Exception) {
    if (other.doesNotExist) {
        other.parent?.createDirectories()
        Files.move(this, other)
    } else {
        val isSameFile = this.isSameFileAs(other)

        if (isSameFile) {
            if (this.toAbsolutePath().toString() != other.toAbsolutePath().toString()) {
                // this can happen for example when changing letter casing, on a case-insensitive file system

                other.parent?.createDirectories()

                val tempFile = other.resolveSibling(UUID.randomUUID().toString())

                Files.move(this, tempFile)
                Files.move(tempFile, other)
            }
        } else {
            throw createDestinationExistsException()
        }
    }
}

fun JavaPath.readAllLines(charset: Charset = Charsets.UTF_8) = Files.readAllLines(this, charset)

fun JavaPath.deleteIfExists(): Boolean = Files.deleteIfExists(this)
fun JavaPath.delete(): Unit = Files.delete(this)
fun JavaPath.deleteRecursivelyIfExists() {
    if (doesNotExist) {
        return
    }
    if (!isDirectory) {
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

fun JavaPath.walkFileTree(visitor: FileVisitor<JavaPath>) = Files.walkFileTree(this, visitor)

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
