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
import java.nio.file.Path as JavaPath

class RecentProjectsFileService(private val testerumProjectFileService: TesterumProjectFileService) {

    companion object {
        private val NO_RECENT_PROJECTS = RecentProjectsLoadResult(
                validProjects = emptyList(),
                invalidProjects = emptyList()
        )

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

    data class RecentProjectsLoadResult(val validProjects: List<RecentProject>,
                                        val invalidProjects: List<RecentProject>)

    fun save(recentProjects: List<RecentProject>,
             recentProjectsFile: JavaPath) {
        recentProjectsFile.parent?.createDirectories()
        OBJECT_MAPPER.writeValue(recentProjectsFile.toFile(), recentProjects)
    }

    fun load(recentProjectsFile: JavaPath): RecentProjectsLoadResult {
        if (recentProjectsFile.doesNotExist) {
            return NO_RECENT_PROJECTS
        }

        val validProjects = ArrayList<RecentProject>()
        val invalidProjects = ArrayList<RecentProject>()

        val recentProjects: List<RecentProject> = OBJECT_MAPPER.readValue(recentProjectsFile.toFile())
        for (recentProject in recentProjects) {
            if (recentProject.isValid()) {
                validProjects += recentProject
            } else {
                invalidProjects += recentProject
            }
        }

        return RecentProjectsLoadResult(
                validProjects = validProjects,
                invalidProjects = invalidProjects
        )
    }

    private fun RecentProject.isValid(): Boolean {
        return testerumProjectFileService.isTesterumProject(path)
    }

}
