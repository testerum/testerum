package com.testerum.web_backend.services.project

import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.exception.ValidationException
import com.testerum.model.home.Project
import com.testerum.model.project.FileProject
import com.testerum.model.project.RecentProject
import com.testerum.project_manager.ProjectManager
import com.testerum.web_backend.controllers.project.model.CreateProjectRequest
import com.testerum.web_backend.services.dirs.FrontendDirs
import org.slf4j.LoggerFactory
import java.nio.file.AccessDeniedException
import java.nio.file.Paths
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class ProjectFrontendService(private val frontendDirs: FrontendDirs,
                             private val recentProjectsFileService: RecentProjectsFileService,
                             private val testerumProjectFileService: TesterumProjectFileService,
                             private val projectManager: ProjectManager) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProjectFrontendService::class.java)
    }

    fun getProjects(): List<Project> {
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        val result = ArrayList<Project>()

        val recentProjects = recentProjectsFileService.load(recentProjectsFile)

        val recentProjectsToReSave = ArrayList<RecentProject>()

        for (recentProject in recentProjects) {
            val fileProject: FileProject? = loadFileProjectSafely(recentProject.path)

            if (fileProject != null) {
                recentProjectsToReSave += recentProject

                val path = recentProject.path.toAbsolutePath().normalize().toString()

                result += Project(
                        name = fileProject.name,
                        path = path,
                        lastOpened = recentProject.lastOpened
                )
            }
        }

        if (recentProjectsToReSave.size != recentProjects.size) {
            // some paths are no longer valid; update
            recentProjectsFileService.save(recentProjectsToReSave, recentProjectsFile)
        }

        return result
    }

    private fun loadFileProjectSafely(directory: JavaPath): FileProject? {
        return try {
            testerumProjectFileService.load(directory)
        } catch (e: Exception) {
            null
        }
    }

    fun createProject(createProjectRequest: CreateProjectRequest): Project {
        val projectRootDir: JavaPath = Paths.get(createProjectRequest.projectParentDir).resolve(
                createProjectRequest.projectName
        )
        val absoluteProjectRootDir: JavaPath = projectRootDir.toAbsolutePath().normalize()

        // validate
        if (testerumProjectFileService.isTesterumProject(absoluteProjectRootDir)) {
            throw ValidationException(
                    globalMessage = "The directory [$absoluteProjectRootDir] is already a Testerum project.",
                    globalHtmlMessage = "The directory <br/><code>$absoluteProjectRootDir</code><br/>is already a Testerum project.")
        }

        // create project file & directory
        val savedFileProject: FileProject = try {
            testerumProjectFileService.save(
                           fileProject = FileProject(
                                   name = createProjectRequest.projectName,
                                   id = testerumProjectFileService.generateProjectId()
                           ),
                           directory = absoluteProjectRootDir
                   )
        } catch (e: AccessDeniedException) {
            LOG.warn("filesystem permissions do not allow to create a project; createProjectRequest=$createProjectRequest", e)

            throw ValidationException(
                    globalMessage = "File system permissions do not allow creation of a Testerum project in the directory [$absoluteProjectRootDir].",
                    globalHtmlMessage = "File system permissions do not allow creation of a Testerum project in the directory <br/><code>$absoluteProjectRootDir</code><br/>.")
        }

        val recentProject = RecentProject(
                path = absoluteProjectRootDir,
                lastOpened = LocalDateTime.now()
        )

        // add to recent projects list
        recentProjectsFileService.add(recentProject, frontendDirs.getRecentProjectsFile())

        return mapToProject(savedFileProject, recentProject)
    }

    fun openProject(path: String): Project {
        val projectRootDir: JavaPath = Paths.get(path)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        // load file project
        val fileProject = testerumProjectFileService.load(projectRootDir)

        // load recent project
        val recentProject = recentProjectsFileService.updateLastOpened(projectRootDir, recentProjectsFile)

        return mapToProject(fileProject, recentProject)
    }

    fun deleteRecentProject(path: String) {
        val projectRootDir: JavaPath = Paths.get(path)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        recentProjectsFileService.delete(projectRootDir, recentProjectsFile)
    }

    fun renameProject(project: Project): Project {
        val projectRootDir: JavaPath = Paths.get(project.path)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        // update project file
        val existingFileProject: FileProject = testerumProjectFileService.load(projectRootDir)
        val newFileProject = existingFileProject.copy(
                name = project.name
        )

        val savedFileProject = testerumProjectFileService.save(newFileProject, projectRootDir)

        // load recent project
        val recentProject = recentProjectsFileService.getByPathOrAdd(projectRootDir, recentProjectsFile)

        // re-load project cache, because the name is cached
        projectManager.closeProject(projectRootDir)

        return mapToProject(savedFileProject, recentProject)
    }

    private fun mapToProject(fileProject: FileProject,
                             recentProject: RecentProject): Project {
        return Project(
                name = fileProject.name,
                path = recentProject.path.toString(),
                lastOpened = recentProject.lastOpened
        )
    }
}
