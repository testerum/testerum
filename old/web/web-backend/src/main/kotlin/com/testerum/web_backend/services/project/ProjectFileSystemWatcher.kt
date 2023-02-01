package com.testerum.web_backend.services.project

import com.testerum.common_fsnotifier.dirty_dirs_tracker.DirtyDirsTracker
import com.testerum.model.project.FileProject
import com.testerum.project_manager.ProjectManager
import org.slf4j.LoggerFactory
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

typealias ReloadListener = (projectRootDir: JavaPath) -> Unit

class ProjectFileSystemWatcher(fsNotifierBinariesDir: JavaPath,
                               private val projectManager: ProjectManager) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectFileSystemWatcher::class.java)

        private const val RELOAD_MINIMUM_MILLIS_SINCE_LAST_CHANGE = 1000
    }

    private val dirtyDirsTracker = DirtyDirsTracker(
            fsNotifierBinariesDir = fsNotifierBinariesDir,
            ignoredDirs = setOf(
                    ".git",
                    ".svn",
                    ".hg",
                    ".bzr"
            )
    )
    private val timer = Timer()

    private val lock = ReentrantReadWriteLock()

    private val lastProjectChange = LinkedHashMap<JavaPath, /*System.currentTimeMillis() at the time of change:*/Long>()

    private val reloadListeners = ArrayList<ReloadListener>()

    init {
        projectManager.registerOpenListener(this::onProjectOpened)
        projectManager.registerCloseListener(this::onProjectClosed)
    }

    fun registerReloadListener(reloadListener: ReloadListener) {
        lock.write {
            reloadListeners += reloadListener
        }
    }

    fun start() {
        lock.write {
            dirtyDirsTracker.start()

            val reloadProjectsTask = object : TimerTask() {
                override fun run() {
                    val currentTime = System.currentTimeMillis()
                    val lastProjectChange = LinkedHashMap(
                            lock.read { lastProjectChange }
                    )
                    for ((path, changeTime) in lastProjectChange) {
                        val timeSinceLastChange = currentTime - changeTime

                        if (timeSinceLastChange >= RELOAD_MINIMUM_MILLIS_SINCE_LAST_CHANGE) {
                            reloadProject(path, changeTime)
                        }
                    }
                }
            }

            timer.scheduleAtFixedRate(reloadProjectsTask, 0, RELOAD_MINIMUM_MILLIS_SINCE_LAST_CHANGE.toLong() / 2)
        }
    }

    fun pause() {
        LOG.debug("pausing ProjectFileSystemWatcher...")
        dirtyDirsTracker.pause()
    }

    fun resume() {
        dirtyDirsTracker.resume()
        LOG.debug("...resumed ProjectFileSystemWatcher")
    }

    fun shutdown() {
        lock.write {
            lastProjectChange.clear()

            timer.cancel()
            dirtyDirsTracker.shutdown()
        }
    }

    private fun onProjectOpened(projectRootDir: JavaPath, fileProject: FileProject) {
        lock.write {
            LOG.info("started watching project $fileProject at path [$projectRootDir] for file changes")
            dirtyDirsTracker.addWatch(projectRootDir, this::onProjectChanged)
        }
    }

    private fun onProjectChanged(projectRootDir: JavaPath) {
        lock.write {
            lastProjectChange[projectRootDir] = System.currentTimeMillis()
        }
    }

    private fun onProjectClosed(projectRootDir: JavaPath, fileProject: FileProject) {
        lock.write {
            dirtyDirsTracker.removeWatch(projectRootDir)
            LOG.info("stopped watching project $fileProject at path [$projectRootDir] for file changes")
        }
    }

    private fun reloadProject(projectRootDir: JavaPath, reloadRequestTime: Long) {
        lock.write {
            LOG.info("project at path [$projectRootDir] changed: reloading...")
            projectManager.reloadProject(projectRootDir)

            val currentReloadRequestTime = lastProjectChange[projectRootDir]

            if (currentReloadRequestTime == reloadRequestTime) {
                lastProjectChange.remove(projectRootDir)
            }

            for (reloadListener in reloadListeners) {
                reloadListener(projectRootDir)
            }
        }
    }
}
