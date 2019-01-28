package com.testerum.file_service.caches

import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.exception.ValidationException
import com.testerum.model.home.Project
import com.testerum.model.project.FileProject
import com.testerum.model.project.RecentProject
import org.slf4j.LoggerFactory
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class RecentProjectsCache(private val recentProjectsFileService: RecentProjectsFileService,
                          private val testerumProjectFileService: TesterumProjectFileService) {

    // todo: refactor this class

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

            this.projectsByPath = load(recentProjectsFile).groupByPath()

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${projectsByPath.size} recent projects took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    private fun List<Project>.groupByPath(): MutableMap<String, Project> {
        val result = HashMap<String, Project>()

        for (project in this) {
            result[project.path] = project
        }

        return result
    }

    private fun load(recentProjectsFile: JavaPath): List<Project> {
        val result = ArrayList<Project>()

        val loadResult = recentProjectsFileService.load(recentProjectsFile)

        var shouldReSave = false
        if (loadResult.invalidProjects.isNotEmpty()) {
            shouldReSave = true
        }

        for (recentProject in loadResult.validProjects.sortedByDescending { it.lastOpened }) {
            val fileProject: FileProject? = loadProjectSafely(recentProject.path)

            if (fileProject == null) {
                // invalid project file: remove from list of projects to re-save
                shouldReSave = true
            } else {
                // if there are multiple projects with the same name, only use the first one
                // since the list is sorted by lastOpened descending, this is the most recently opened project
                result += mapFileAndRecentToProject(fileProject, recentProject)
            }
        }

        if (shouldReSave) {
            val recentProjectsToReSave = result
                    .map { it.toRecentProject() }
                    .toList()
                    .sortedByDescending { it.lastOpened }

            recentProjectsFileService.save(recentProjectsToReSave, recentProjectsFile)
        }

        return result
    }

    private fun loadProjectSafely(directory: JavaPath): FileProject? {
        return try {
            testerumProjectFileService.load(directory)
        } catch (e: Exception) {
            null
        }
    }

    fun getAllProjects(): Collection<Project> = lock.read { projectsByPath.values }

    fun createProject(projectParentDir: JavaPath,
                      projectName: String): Project {
        lock.write {
            val projectRootDir = projectParentDir.resolve(projectName)
            val absoluteProjectRootDir = projectRootDir.toAbsolutePath().normalize()

            if (testerumProjectFileService.isTesterumProject(absoluteProjectRootDir)) {
                throw ValidationException(
                        globalMessage = "The directory [$absoluteProjectRootDir] is already a Testerum project.",
                        globalHtmlMessage = "The directory <br/><code>$absoluteProjectRootDir</code><br/>is already a Testerum project.")
            }

            val savedFileProject = testerumProjectFileService.save(
                    fileProject = FileProject(name = projectName),
                    directory = absoluteProjectRootDir
            )

            val savedRecentProject = RecentProject(
                    path = absoluteProjectRootDir,
                    lastOpened = LocalDateTime.now()
            )

            val savedProject = mapFileAndRecentToProject(savedFileProject, savedRecentProject)

            projectsByPath[savedProject.path] = savedProject

            val recentProjectsToReSave = projectsByPath.values.map { it.toRecentProject() }

            recentProjectsFileService.save(recentProjectsToReSave, recentProjectsFile!!)

            return savedProject
        }
    }

    fun openProject(path: String): Project {
        lock.write {
            val absoluteProjectRootDir = Paths.get(path).toAbsolutePath().normalize()

            if (!testerumProjectFileService.isTesterumProject(absoluteProjectRootDir)) {
                throw ValidationException(
                        globalMessage = "The directory [$absoluteProjectRootDir] is not a Testerum project.",
                        globalHtmlMessage = "The directory <br/><code>$absoluteProjectRootDir</code><br/>is not a Testerum project.")
            }

            val loadedFileProject = testerumProjectFileService.load(absoluteProjectRootDir)

            val recentProject = RecentProject(
                    path = absoluteProjectRootDir,
                    lastOpened = LocalDateTime.now()
            )

            val project = mapFileAndRecentToProject(loadedFileProject, recentProject)

            projectsByPath[project.path] = project

            val recentProjectsToReSave = projectsByPath.values.map { it.toRecentProject() }

            recentProjectsFileService.save(recentProjectsToReSave, recentProjectsFile!!)

            return project
        }
    }

    private fun mapFileAndRecentToProject(fileProject: FileProject,
                                          recentProject: RecentProject): Project {
        val path = recentProject.path.toAbsolutePath().normalize().toString()

        return Project(
                name = fileProject.name,
                path = path,
                lastOpened = recentProject.lastOpened
        )
    }

    private fun Project.toRecentProject(): RecentProject {
        return RecentProject(
                path = Paths.get(this.path).toAbsolutePath().normalize(),
                lastOpened = this.lastOpened
        )
    }

}
