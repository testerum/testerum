package com.testerum.common_fsnotifier.dirty_dirs_tracker

import com.testerum.common_fsnotifier.native_fs_notifier.NativeFsNotifier
import com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter.FsNotifierEventListener
import com.testerum.common_kotlin.canonicalize
import com.testerum.common_kotlin.isDirectory
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

typealias DirtyPathListener = (path: JavaPath) -> Unit

class DirtyDirsTracker(fsNotifierBinariesDir: JavaPath,
                       private val ignoredDirs: Set<String>) {

    companion object {
        private val LOG = LoggerFactory.getLogger(DirtyDirsTracker::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var paused: Boolean = true
    private val dirtyListeners = HashMap<JavaPath, MutableList<DirtyPathListener>>()

    private val fsNotifier = NativeFsNotifier(fsNotifierBinariesDir).apply {
        addListener(object : FsNotifierEventListener {
            override fun onGiveUp() {
                LOG.error("native FS notifier gave up; Testerum will not notice when files change")
                this@DirtyDirsTracker.shutdown()
            }

            override fun onReset(path: JavaPath?) {
                lock.write {
                    setRoots()
                }
            }

            override fun onFailureMessage(message: String) {
                LOG.warn("[fsnotifier] $message")
            }

            override fun onMapping(roots: Collection<Pair<JavaPath, JavaPath>>) {
                // we are not interested in this event
                // it only informs us of Windows SUBST drives, not a FileSystem change
            }

            override fun onUnwatchable(roots: Collection<JavaPath>) {
                LOG.warn(
                        "[fsnotifier] there unwatchable paths" +
                        "; Testerum won't be able to notice when files change under these paths" +
                        "; unwatchable paths: $roots"
                )
            }

            override fun onDirtyPath(path: JavaPath) {
                LOG.debug("onDirtyPath($path)")
                notifyDirty(path)
            }

            override fun onDirtyPathRecursive(path: JavaPath) {
                LOG.debug("onDirtyPathRecursive($path)")
                notifyDirty(path)
            }

            override fun onDirtyDirectory(path: JavaPath) {
                LOG.debug("onDirtyDirectory($path)")
                notifyDirty(path)
            }

            override fun onCreateOrDelete(path: JavaPath) {
                LOG.debug("onCreateOrDelete($path)")
                notifyDirty(path)
            }
        })
    }

    fun start() {
        lock.write {
            fsNotifier.start()
            paused = false
        }
    }

    fun addWatch(path: JavaPath,
                 listener: DirtyPathListener) {
        val canonicalPath = path.canonicalize()

        lock.write {
            dirtyListeners.compute(canonicalPath) { _, existingListeners ->
                if (existingListeners == null) {
                    val newListeners = ArrayList<DirtyPathListener>()
                    newListeners += listener

                    newListeners
                } else {
                    existingListeners.add(listener)

                    existingListeners
                }
            }

            setRoots()
        }
    }

    fun removeWatch(path: JavaPath) {
        val canonicalPath = path.canonicalize()

        lock.write {
            dirtyListeners.remove(canonicalPath)

            fsNotifier.setRoots(
                    recursiveRoots = dirtyListeners.keys.toList(),
                    flatRoots = emptyList()
            )
        }
    }

    fun shutdown() {
        lock.write {
            fsNotifier.shutdown()
            paused = true
        }
    }

    fun pause() {
        lock.write {
            if (this.paused) {
                return
            }

            fsNotifier.setRoots(
                    recursiveRoots = emptyList(),
                    flatRoots = emptyList()
            )
            this.paused = true
        }
    }

    fun resume() {
        lock.write {
            fsNotifier.setRoots(
                    recursiveRoots = dirtyListeners.keys.toList(),
                    flatRoots = emptyList()
            )
            this.paused = false
        }
    }

    private fun setRoots() {
        fsNotifier.setRoots(
                recursiveRoots = dirtyListeners.keys.toList(),
                flatRoots = emptyList()
        )
    }

    private fun notifyDirty(dirtyPath: JavaPath) {
        val canonicalDirtyPath = dirtyPath.canonicalize()
        if (isIgnoredPath(canonicalDirtyPath)) {
            return
        }

        lock.read {
            if (paused) {
                return
            }

            for ((watchedPath, recorders) in dirtyListeners) {
                val canonicalWatchedPath = watchedPath.canonicalize()

                if (canonicalDirtyPath.startsWith(canonicalWatchedPath)) {
                    recorders.forEach { it(canonicalWatchedPath) }
                }
            }
        }
    }

    private fun isIgnoredPath(path: JavaPath): Boolean {
        for (pathSegment: JavaPath in path) {
            if (!pathSegment.isDirectory) {
                continue
            }

            for (ignoredDir in ignoredDirs) {
                if (pathSegment.fileName?.toString() == ignoredDir) {
                    return true
                }
            }
        }

        return false
    }

}
