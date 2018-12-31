package com.testerum.common_kotlin

import com.testerum.common_jdk.OsUtils
import java.nio.file.Files
import java.nio.file.Path as JavaPath

object PathUtils {

    fun createOrUpdateSymbolicLink(absoluteSymlinkPath: JavaPath,
                                   absoluteTarget: JavaPath) {
        if (!absoluteSymlinkPath.isAbsolute) {
            throw IllegalArgumentException("absoluteSymlinkPath is not absolute: [$absoluteSymlinkPath]")
        }
        if (!absoluteTarget.isAbsolute) {
            throw IllegalArgumentException("absoluteTarget is not absolute: [$absoluteTarget]")
        }

        absoluteSymlinkPath.deleteIfExists()

        if (OsUtils.IS_WINDOWS) {
            // On Windows, a regular user is not allowed to create a symlink.
            // We will create a junction instead

            val command = arrayOf(
                    "cmd",
                    "/C",
                    """mklink /J "$absoluteSymlinkPath" "$absoluteTarget""""
            )

            Runtime.getRuntime().exec(command)
        } else {
            val relativeTargetPath: JavaPath = absoluteSymlinkPath.relativize(absoluteTarget)

            Files.createSymbolicLink(absoluteSymlinkPath, relativeTargetPath)
        }
    }

}
