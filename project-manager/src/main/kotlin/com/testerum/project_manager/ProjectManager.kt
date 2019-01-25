package com.testerum.project_manager

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import java.util.concurrent.TimeUnit
import java.nio.file.Path as JavaPath

class ProjectManager(private val createFeaturesCache: (ProjectServices) -> FeaturesCache,
                     private val createTestsCache: (ProjectServices) -> TestsCache,
                     private val createStepsCache: (ProjectServices) -> StepsCache) {

    private val openProjects: LoadingCache</*projectRootDir: */JavaPath, ProjectServices> = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .build(object : CacheLoader<JavaPath, ProjectServices>() {
                override fun load(projectRootDir: JavaPath): ProjectServices = openProject(projectRootDir)
            })

    fun getProjectServices(projectRootDir: JavaPath): ProjectServices = openProjects[projectRootDir]

    private fun openProject(projectRootDir: JavaPath): ProjectServices {
        return ProjectServices(projectRootDir, createFeaturesCache, createTestsCache, createStepsCache)
    }

}
