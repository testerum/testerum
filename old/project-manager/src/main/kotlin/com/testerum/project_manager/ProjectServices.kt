package com.testerum.project_manager

import com.testerum.file_service.caches.resolved.FeaturesCache
import com.testerum.file_service.caches.resolved.StepsCache
import com.testerum.file_service.caches.resolved.TestsCache
import com.testerum.model.project.FileProject
import com.testerum.project_manager.dirs.ProjectDirs
import java.nio.file.Path

class ProjectServices(
    projectRootDir: Path,
    val project: FileProject,
    private val packagesWithAnnotations: List<String>,
    createFeaturesCache: (ProjectServices) -> FeaturesCache,
    createTestsCache: (ProjectServices) -> TestsCache,
    createStepsCache: (ProjectServices) -> StepsCache
) {

    private val dirs = ProjectDirs(projectRootDir)

    // todo: don't pass a ProjectServices reference to the factory methods, since this can create initialization problems, leading to NPEs

    private val stepsCache = createStepsCache(this).apply {
        initializeStepsCacheFromProjectServices()
    }

    private val testsCache = createTestsCache(this).apply {
        initializeTestsCacheFromProjectServices()
    }

    private val featuresCache = createFeaturesCache(this).apply {
        initializeFeatureCacheFromProjectServices()
    }

    fun dirs() = dirs

    fun getFeatureCache(): FeaturesCache = featuresCache

    fun reinitializeFeatureCache() {
        featuresCache.initializeFeatureCacheFromProjectServices()
    }

    fun getTestsCache(): TestsCache = testsCache

    fun reinitializeTestsCache() {
        testsCache.initializeTestsCacheFromProjectServices()
    }

    fun getStepsCache(): StepsCache = stepsCache

    fun reinitializeStepsCache() {
        stepsCache.initializeStepsCacheFromProjectServices()
    }

    private fun FeaturesCache.initializeFeatureCacheFromProjectServices() {
        val featuresDir = dirs.getFeaturesDir()
        val resourcesDir = dirs.getResourcesDir()

        initialize(featuresDir, resourcesDir)
    }

    private fun TestsCache.initializeTestsCacheFromProjectServices() {
        val testsDir = dirs.getTestsDir()
        val resourcesDir = dirs.getResourcesDir()

        initialize(testsDir, resourcesDir)
    }

    private fun StepsCache.initializeStepsCacheFromProjectServices() {
        initialize(
            packagesWithAnnotations = packagesWithAnnotations,
            additionalBasicStepsDirs = listOf(
                dirs.getAdditionalBasicStepsDir()
            ),
            composedStepsDir = dirs.getComposedStepsDir(),
            resourcesDir = dirs.getResourcesDir()
        )
    }

}
