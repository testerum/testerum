package com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter

import java.nio.file.Path as JavaPath

interface FsNotifierEventListener {

    fun onGiveUp()

    fun onReset(path: JavaPath?)

    fun onFailureMessage(message: String)

    /**
     * Windows only event; informs us of real paths for paths on SUBST drives
     */
    fun onMapping(roots: Collection<Pair<JavaPath, JavaPath>>)

    fun onUnwatchable(roots: Collection<JavaPath>)

    fun onDirtyPath(path: JavaPath)

    fun onDirtyPathRecursive(path: JavaPath)

    fun onDirtyDirectory(path: JavaPath)

    fun onCreateOrDelete(path: JavaPath)

}
