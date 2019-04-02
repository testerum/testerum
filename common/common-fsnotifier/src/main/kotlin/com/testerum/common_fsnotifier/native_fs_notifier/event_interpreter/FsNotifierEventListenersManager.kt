package com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter

import java.nio.file.Path as JavaPath

class FsNotifierEventListenersManager {

    private val listeners = ArrayList<FsNotifierEventListener>()

    fun addListener(listener: FsNotifierEventListener) {
        listeners += listener
    }

    fun notifyGiveUp() {
        for (listener in listeners) {
            listener.onGiveUp()
        }
    }

    fun notifyReset(path: JavaPath?) {
        for (listener in listeners) {
            listener.onReset(path)
        }
    }

    fun notifyFailureMessage(message: String) {
        for (listener in listeners) {
            listener.onFailureMessage(message)
        }
    }

    fun notifyMapping(roots: Collection<Pair<JavaPath, JavaPath>>) {
        for (listener in listeners) {
            listener.onMapping(roots)
        }
    }

    fun notifyUnwatchable(roots: Collection<JavaPath>) {
        for (listener in listeners) {
            listener.onUnwatchable(roots)
        }
    }

    fun notifyDirtyPath(path: JavaPath) {
        for (listener in listeners) {
            listener.onDirtyPath(path)
        }
    }

    fun notifyDirtyPathRecursive(path: JavaPath) {
        for (listener in listeners) {
            listener.onDirtyPathRecursive(path)
        }
    }

    fun notifyDirtyDirectory(path: JavaPath) {
        for (listener in listeners) {
            listener.onDirtyDirectory(path)
        }
    }

    fun notifyCreateOrDelete(path: JavaPath) {
        for (listener in listeners) {
            listener.onCreateOrDelete(path)
        }
    }

}
