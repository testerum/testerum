package com.testerum.common_kotlin

import com.testerum.common_jdk.OsUtils
import java.nio.file.Files
import java.nio.file.Path as JavaPath

object PathUtils {

    fun createOrUpdateSymbolicLink(absoluteSymlinkPath: JavaPath,
                                   absoluteTarget: JavaPath,
                                   symlinkRelativeTo: JavaPath? = null) {
        if (!absoluteSymlinkPath.isAbsolute) {
            throw IllegalArgumentException("absoluteSymlinkPath is not absolute: [$absoluteSymlinkPath]")
        }
        if (!absoluteTarget.isAbsolute) {
            throw IllegalArgumentException("absoluteTarget is not absolute: [$absoluteTarget]")
        }

        absoluteSymlinkPath.deleteIfExists()

        val actualTargetPath: JavaPath = if (symlinkRelativeTo == null) {
            absoluteTarget
        } else {
            symlinkRelativeTo.relativize(absoluteTarget)
        }

        if (OsUtils.IS_WINDOWS) {
            // On Windows, a regular user is not allowed to create a symlink.
            // We will create a junction instead

            val command = arrayOf(
                    "cmd",
                    "/C",
                    """mklink /J "$absoluteSymlinkPath" "$actualTargetPath""""
            )

            Runtime.getRuntime().exec(command, null, symlinkRelativeTo?.toFile())
        } else {
            Files.createSymbolicLink(absoluteSymlinkPath, actualTargetPath)
        }
    }

}
