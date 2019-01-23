package com.testerum.file_service.caches

import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.home.Project
import com.testerum.model.project.RecentProject
import com.testerum.model.project.TesterumProject
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class RecentProjectsCache(private val recentProjectsFileService: RecentProjectsFileService,
                          private val testerumProjectFileService: TesterumProjectFileService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(RecentProjectsCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var recentProjectsFile: JavaPath? = null

    private var projectsByPath: MutableMap<String, Project> = HashMap()

    fun initialize(recentProjectsFile: JavaPath) {
        lock.write {
            val startTimeMillis = System.currentTimeMillis()

            this.recentProjectsFile = recentProjectsFile

            this.projectsByPath = load(recentProjectsFile)

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${projectsByPath.size} recent projects took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    private fun load(recentProjectsFile: JavaPath): MutableMap<String, Project> {
        val result = HashMap<String, Project>()

        val loadResult = recentProjectsFileService.load(recentProjectsFile)

        var shouldReSave = false
        if (loadResult.invalidProjects.isNotEmpty()) {
            shouldReSave = true
        }

        for (recentProject in loadResult.validProjects.sortedByDescending { it.lastOpened }) {
            val testerumProject: TesterumProject? = loadProjectSafely(recentProject.path)

            if (testerumProject == null) {
                // invalid project file: remove from list of projects to re-save
                shouldReSave = true
            } else {
                // if there are multiple projects with the same name, only use the first one
                // since the list is sorted by lastOpened descending, this is the most recently opened project
                // todo: move this to a Mapper class?
                val path = recentProject.path.toString()

                result[path] = Project(
                        name = testerumProject.name,
                        path = path,
                        lastOpened = recentProject.lastOpened
                )
            }
        }

        if (shouldReSave) {
            val recentProjectsToReSave = result.values
                    .map {
                        // todo: move this to a Mapper class? or to a method of Project?
                        RecentProject(
                                path = Paths.get(it.path),
                                lastOpened = it.lastOpened
                        )
                    }
                    .toList()
                    .sortedByDescending { it.lastOpened }

            recentProjectsFileService.save(recentProjectsToReSave, recentProjectsFile)
        }

        return result
    }

    private fun loadProjectSafely(directory: JavaPath): TesterumProject? {
        return try {
            testerumProjectFileService.load(directory)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllProjects(): Collection<Project> = lock.read { projectsByPath.values }

}
