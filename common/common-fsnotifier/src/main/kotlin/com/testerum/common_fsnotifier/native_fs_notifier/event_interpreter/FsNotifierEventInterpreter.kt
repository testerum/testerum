package com.testerum.common_fsnotifier.native_fs_notifier.event_interpreter

import com.testerum.common_jdk.OsUtils
import com.testerum.common_kotlin.canonicalize
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths
import java.text.Normalizer
import java.util.Arrays
import java.util.HashSet
import java.nio.file.Path as JavaPath

class FsNotifierEventInterpreter {

    companion object {
        private val LOG = LoggerFactory.getLogger(FsNotifierEventInterpreter::class.java)
    }

    private var recursiveRoots: List<JavaPath> = emptyList()
    private var flatRoots: List<JavaPath> = emptyList()

    private val listenersManager = FsNotifierEventListenersManager()
    private var lastEventType: FsNotifierEventType? = null
    private val lines = ArrayList<String>()

    fun addListener(listener: FsNotifierEventListener) = listenersManager.addListener(listener)

    /**
     * @param recursiveRoots paths to watch for changes recursively
     * @param flatRoots      paths to watch for changes only in direct children
     */
    fun setRoots(recursiveRoots: List<JavaPath>,
                 flatRoots: List<JavaPath>) {
        this.recursiveRoots = recursiveRoots.map {
            it.canonicalize()
        }
        this.flatRoots = flatRoots.map {
            it.canonicalize()
        }
    }

    private fun JavaPath.isInteresting(): Boolean = isInterestingFlat() || isBelowRecursiveRoot()

    private fun JavaPath.isInterestingFlat(): Boolean {
        for (flatRoot in flatRoots) {
            if (this.parent == flatRoot) {
                return true
            }
        }

        return false
    }

    private fun JavaPath.isBelowRecursiveRoot(): Boolean {
        for (recursiveRoot in recursiveRoots) {
            if (this.startsWith(recursiveRoot)) {
                return true
            }
        }

        return false
    }

    private fun JavaPath.isAboveRecursiveRoot(): Boolean {
        for (recursiveRoot in recursiveRoots) {
            if (recursiveRoot.startsWith(this)) {
                return true
            }
        }

        return false
    }

    fun onLineReceived(line: String) {
        LOG.debug("[fsnotifier-line] $line")

        when (lastEventType) {
            null -> {
                val eventType = safelyParseEventType(line)
                        ?: return

                when (eventType) {
                    FsNotifierEventType.GIVEUP -> listenersManager.notifyGiveUp()
                    FsNotifierEventType.RESET -> listenersManager.notifyReset(null)
                    else -> lastEventType = eventType
                }
            }

            FsNotifierEventType.MESSAGE -> {
                LOG.warn("[fsnotifier] $line")
                listenersManager.notifyFailureMessage(line)
                lastEventType = null
            }

            FsNotifierEventType.REMAP, FsNotifierEventType.UNWATCHEABLE -> {
                if (line == "#") {
                    if (lastEventType == FsNotifierEventType.REMAP) {
                        processRemap()
                    } else {
                        processUnwatchable()
                    }

                    lines.clear()
                    lastEventType = null
                } else {
                    lines += line
                }
            }

            else -> {
                val path = line.unescapePath()
                processChange(path, lastEventType)
                lastEventType = null
            }
        }
    }

    private fun processRemap() {
        val mappings = HashSet<Pair<JavaPath, JavaPath>>()
        for (i in 0 until (lines.size - 1) step 2) {

            mappings += Pair(
                    lines[i].toCanonicalJavaPath(),
                    lines[i + 1].toCanonicalJavaPath()
            )
        }

        if (mappings.isNotEmpty()) {
            listenersManager.notifyMapping(mappings)
        }
    }

    private fun processUnwatchable() {
        val roots = lines.map { Paths.get(it) }

        val filteredRoots = roots.filter {
            it.isInteresting()
        }
        if (filteredRoots.isNotEmpty()) {
            listenersManager.notifyUnwatchable(filteredRoots)
        }
    }

    private fun processChange(path: String, eventType: FsNotifierEventType?) {
        val normalizedPath: String = if (OsUtils.IS_MAC) {
            Normalizer.normalize(path, Normalizer.Form.NFC)
        } else {
            path
        }
        val normalizedJavaPath: JavaPath = normalizedPath.toCanonicalJavaPath()

        when (eventType) {
            FsNotifierEventType.STATS, FsNotifierEventType.CHANGE -> {
                if (normalizedJavaPath.isInteresting()) {
                    listenersManager.notifyDirtyPath(normalizedJavaPath)
                }
            }

            FsNotifierEventType.CREATE, FsNotifierEventType.DELETE -> {
                if (normalizedJavaPath.isInteresting()) {
                    listenersManager.notifyCreateOrDelete(normalizedJavaPath)
                }
            }

            FsNotifierEventType.DIRTY -> {
                if (normalizedJavaPath.isInteresting()) {
                    listenersManager.notifyDirtyDirectory(normalizedJavaPath)
                }
            }

            FsNotifierEventType.RECDIRTY -> {
                if (normalizedJavaPath.isInteresting() || normalizedJavaPath.isAboveRecursiveRoot()) {
                    listenersManager.notifyDirtyPathRecursive(normalizedJavaPath)
                }
            }

            else -> LOG.error("unexpected fsnotifier event type [$eventType]")
        }
    }

    private fun safelyParseEventType(line: String): FsNotifierEventType? {
        return try {
            FsNotifierEventType.valueOf(line)
        } catch (e: Exception) {
            val errorMessage = buildString {
                append("illegal FS notifier event type [").append(line).append("]")

                if (line.length <= 20) {
                    append(" ")
                    append(Arrays.toString(line.chars().toArray()))
                }
            }

            LOG.error(errorMessage)

            null
        }
    }

    private fun String.unescapePath() = this.replace('\u0000', '\n').removeSuffix(File.separator)

    private fun String.toCanonicalJavaPath(): JavaPath = Paths.get(this).canonicalize()

}
