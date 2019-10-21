package com.testerum.file_service.file

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.common_kotlin.createDirectories
import com.testerum.common_kotlin.doesNotExist
import com.testerum.model.project.RecentProject
import com.testerum.settings.TesterumDirs
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import java.nio.file.Path as JavaPath

class RecentProjectsFileService (private val testerumDirs: TesterumDirs) {

    companion object {
        private val OBJECT_MAPPER: ObjectMapper = jacksonObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    fun save(recentProjects: List<RecentProject>,
             recentProjectsFile: JavaPath) {
        val sortedRecentProjects = recentProjects.sortedByDescending { it.lastOpened }

        recentProjectsFile.parent?.createDirectories()
        OBJECT_MAPPER.writeValue(recentProjectsFile.toFile(), sortedRecentProjects)
    }

    fun load(recentProjectsFile: JavaPath): List<RecentProject> {
        if (recentProjectsFile.doesNotExist) {
            return emptyList()
        }

        val recentProjects: List<RecentProject> = OBJECT_MAPPER.readValue(recentProjectsFile.toFile())

        return recentProjects.sortedByDescending { it.lastOpened }
    }

    fun add(recentProject: RecentProject,
            recentProjectsFile: JavaPath) {
        val recentProjects = ArrayList<RecentProject>()

        recentProjects += load(recentProjectsFile)
        recentProjects += recentProject

        val distinctRecentProjects = recentProjects.withoutDuplicates()

        save(distinctRecentProjects, recentProjectsFile)
    }

    fun updateLastOpened(projectRootDir: JavaPath,
                         recentProjectsFile: JavaPath): RecentProject {

        // to prevent Demo App to end-up in the recent projects
        if (projectRootDir == testerumDirs.getDemoTestsDir()) {
            return RecentProject(testerumDirs.getDemoTestsDir(), LocalDateTime.now())
        }

        val recentProject = getByPathOrAdd(projectRootDir, recentProjectsFile)
        val recentProjectToSave = recentProject.copy(
                lastOpened = LocalDateTime.now()
        )

        add(recentProjectToSave, recentProjectsFile)

        return recentProject
    }

    fun getByPathOrAdd(projectRootDir: JavaPath,
                       recentProjectsFile: JavaPath): RecentProject {
        val existingRecentProjects = load(recentProjectsFile)

        val normalizedPath = projectRootDir.toAbsolutePath().normalize()

        val existingProject = existingRecentProjects.find {
            it.path.toAbsolutePath().normalize() == normalizedPath
        }
        if (existingProject != null) {
            return existingProject
        }

        val newRecentProject = RecentProject(
                path = normalizedPath,
                lastOpened = LocalDateTime.now()
        )

        add(newRecentProject, recentProjectsFile)

        return newRecentProject
    }

    fun delete(projectRootDir: JavaPath,
               recentProjectsFile: JavaPath) {
        val recentProjects = ArrayList<RecentProject>()
        recentProjects += load(recentProjectsFile)

        val normalizedPath = projectRootDir.toAbsolutePath().normalize()

        recentProjects.removeIf {
            it.path.toAbsolutePath().normalize() == normalizedPath
        }

        save(recentProjects, recentProjectsFile)
    }

    private fun MutableList<RecentProject>.withoutDuplicates(): List<RecentProject> {
        // not using "distinctBy()" because we want to keep a particular item: the one with the highest "lastOpened"
        val result = TreeMap<JavaPath, RecentProject>()

        for (recentProject in this) {
            val normalizedPath = recentProject.path.toAbsolutePath().normalize()

            val existingRecentProject = result[normalizedPath]

            val shouldAdd = (existingRecentProject == null)
                    || (recentProject.lastOpened > existingRecentProject.lastOpened)

            if (shouldAdd) {
                result[normalizedPath] = recentProject
            }
        }

        return result.values.toList()
    }

}
