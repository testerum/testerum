package com.testerum.web_backend.services.project

import com.testerum.file_service.file.RecentProjectsFileService
import com.testerum.file_service.file.TesterumProjectFileService
import com.testerum.model.home.Project
import com.testerum.model.project.FileProject
import com.testerum.model.project.RecentProject
import com.testerum.web_backend.controllers.project.model.ProjectRequest
import com.testerum.web_backend.services.dirs.FrontendDirs
import java.nio.file.Paths
import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

class ProjectFrontendService(private val frontendDirs: FrontendDirs,
                             private val recentProjectsFileService: RecentProjectsFileService,
                             private val testerumProjectFileService: TesterumProjectFileService) {

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

    fun createProject(createProjectRequest: ProjectRequest): Project {
        val projectRootDir: JavaPath = Paths.get(createProjectRequest.projectParentDir).resolve(
                createProjectRequest.projectName
        )
        val absoluteProjectRootDir: JavaPath = projectRootDir.toAbsolutePath().normalize()

        // create project file & directory
        val savedFileProject = testerumProjectFileService.save(
                fileProject = FileProject(
                        name = createProjectRequest.projectName,
                        id = testerumProjectFileService.generateProjectId()
                ),
                directory = absoluteProjectRootDir
        )

        val recentProject = RecentProject(
                path = absoluteProjectRootDir,
                lastOpened = LocalDateTime.now()
        )

        // add to recent projects list
        recentProjectsFileService.add(recentProject, absoluteProjectRootDir)

        return mapToProject(savedFileProject, recentProject)
    }

    fun openProject(path: String): Project {
        val projectRootDir: JavaPath = Paths.get(path)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        // load file project
        val fileProject = testerumProjectFileService.load(projectRootDir)

        // load recent project
        val recentProject = recentProjectsFileService.getByPathOrAdd(projectRootDir, recentProjectsFile)
        val recentProjectToSave = recentProject.copy(
                lastOpened = LocalDateTime.now()
        )
        recentProjectsFileService.add(recentProjectToSave, recentProjectsFile)

        return mapToProject(fileProject, recentProject)
    }

    fun deleteRecentProject(path: String) {
        val projectRootDir: JavaPath = Paths.get(path)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        recentProjectsFileService.delete(projectRootDir, recentProjectsFile)
    }

    fun renameProject(renameProjectRequest: ProjectRequest): Project {
        val projectRootDir: JavaPath = Paths.get(renameProjectRequest.projectParentDir)
        val recentProjectsFile = frontendDirs.getRecentProjectsFile()

        // update project file
        val existingFileProject: FileProject = testerumProjectFileService.load(projectRootDir)
        val newFileProject = existingFileProject.copy(
                name = renameProjectRequest.projectName
        )

        val savedFileProject = testerumProjectFileService.save(newFileProject, projectRootDir)

        // load recent project
        val recentProject = recentProjectsFileService.getByPathOrAdd(projectRootDir, recentProjectsFile)

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
