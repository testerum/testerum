package com.testerum.project_manager

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.cache.RemovalNotification
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.project.FileProject
import org.slf4j.LoggerFactory
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import java.nio.file.Path as JavaPath

typealias OpenListener = (projectRootDir: JavaPath, fileProject: FileProject) -> Unit
typealias CloseListener = (projectRootDir: JavaPath, fileProject: FileProject) -> Unit

class ProjectManager(private val testerumProjectFileService: TesterumProjectFileService,
                     private val createFeaturesCache: (ProjectServices) -> FeaturesCache,
                     private val createTestsCache: (ProjectServices) -> TestsCache,
                     private val createStepsCache: (ProjectServices) -> StepsCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectManager::class.java)
    }

    private val openProjectsCache: LoadingCache</*projectRootDir: */JavaPath, ProjectServices> = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .removalListener<JavaPath, ProjectServices>(this::onProjectClosed)
            .build(object : CacheLoader<JavaPath, ProjectServices>() {
                override fun load(projectRootDir: JavaPath): ProjectServices = openProject(projectRootDir)
            })

    private val openListeners = ArrayList<OpenListener>()
    private val closeListeners = ArrayList<CloseListener>()

    init {
        startCacheCleanupThread()
    }

    private fun startCacheCleanupThread() {
        val thread = Thread(
                Runnable {
                    while (true) {
                        openProjectsCache.cleanUp()
                        sleep(30_000)
                    }
                },
                "open-projects-cleanup-thread"
        )

        thread.isDaemon = true

        thread.start()
    }

    fun registerOpenListener(openListener: OpenListener) {
        openListeners += openListener
    }

    fun registerCloseListener(closeListener: CloseListener) {
        closeListeners += closeListener
    }

    fun getProjectServices(projectRootDir: JavaPath): ProjectServices = openProjectsCache[canonicalKey(projectRootDir)]

    fun closeProject(projectRootDir: JavaPath) {
        openProjectsCache.invalidate(
                canonicalKey(projectRootDir)
        )
    }

    fun reloadProject(projectRootDir: JavaPath) {
        LOG.info("reloading project at path [$projectRootDir]...")
        closeProject(projectRootDir)
        getProjectServices(projectRootDir) // to re-open the project
        LOG.info("...done reloading project at path [$projectRootDir]")
    }

    private fun openProject(projectRootDir: JavaPath): ProjectServices {
        val absoluteProjectRootDir = projectRootDir.toAbsolutePath().normalize()

        LOG.info("opening project at path [$absoluteProjectRootDir]...")
        val startTimeMillis = System.currentTimeMillis()

        val fileProject = testerumProjectFileService.load(projectRootDir)
        val projectServices = ProjectServices(projectRootDir, fileProject, createFeaturesCache, createTestsCache, createStepsCache)
        val endTimeInitMillis = System.currentTimeMillis()
        LOG.info("...done opening project at path [$absoluteProjectRootDir] (took ${endTimeInitMillis - startTimeMillis} ms)")

        notifyOpenListeners(projectRootDir, fileProject)

        return projectServices
    }

    private fun notifyOpenListeners(projectRootDir: JavaPath,
                                    fileProject: FileProject) {
        for (openListener in openListeners) {
            try {
                openListener.invoke(projectRootDir, fileProject)
            } catch (e: Exception) {
                LOG.warn("project open ($fileProject): failed to notify open listener $openListener", e)
            }
        }
    }

    private fun onProjectClosed(notification: RemovalNotification<JavaPath, ProjectServices>) {
        val projectRootDir = notification.key
        val fileProject = notification.value.project

        LOG.info("closed project at path [$projectRootDir] (cause: ${notification.cause})")

        notifyCloseListeners(projectRootDir, fileProject)
    }

    private fun notifyCloseListeners(projectRootDir: JavaPath, fileProject: FileProject) {
        for (closeListener in closeListeners) {
            try {
                closeListener.invoke(projectRootDir, fileProject)
            } catch (e: Exception) {
                LOG.warn("project close ($fileProject): failed to notify close listener $closeListener", e)
            }
        }
    }

    private fun canonicalKey(projectRootDir: JavaPath): JavaPath = projectRootDir.toAbsolutePath().normalize()
}
