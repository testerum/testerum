package com.testerum.file_service.caches.resolved

import com.testerum.file_service.caches.resolved.resolvers.FeatureResolver
import com.testerum.file_service.caches.warnings.WarningService
import com.testerum.file_service.file.FeatureFileService
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class FeaturesCache(private val featureFileService: FeatureFileService,
                    private val featureResolver: FeatureResolver,
                    private val warningService: WarningService,
                    private val getStepsCache: () -> StepsCache) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FeaturesCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var featuresByPath: MutableMap<Path, Feature> = hashMapOf()

    private var featuresDir: JavaPath? = null
    private var resourcesDir: JavaPath? = null

    fun initialize(featuresDir: JavaPath, resourcesDir: JavaPath) {
        lock.write {
            val startTimeMillis = System.currentTimeMillis()

            this.featuresDir = featuresDir
            this.resourcesDir = resourcesDir

            val newFeaturesByPath = ConcurrentHashMap<Path, Feature>()

            val features = featureFileService.getAllFeatures(featuresDir)
            for (feature in features) {
                val pathWithoutFile = feature.path.withoutFile()

                var resolvedFeature = feature.copy(
                        path = pathWithoutFile,
                        oldPath = pathWithoutFile
                )

                resolvedFeature = featureResolver.resolveHooks(getStepsCache, resolvedFeature, resourcesDir)
                resolvedFeature = warningService.featureWithWarnings(resolvedFeature)

                newFeaturesByPath[resolvedFeature.path] = resolvedFeature
            }

            featuresByPath = newFeaturesByPath

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${newFeaturesByPath.size} features took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    fun getAllFeatures(): Collection<Feature> = lock.read { featuresByPath.values }

    fun getFeatureAtPath(path: Path): Feature? = lock.read { featuresByPath[path] }

    fun save(featureToSave: Feature, featuresDir: JavaPath): Feature {
        val resourcesDir = this.resourcesDir
            ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")

        lock.write {
            val savedFeature = featureFileService.save(featureToSave, featuresDir)

            var resolvedSavedFeature = featureResolver.resolveHooks(getStepsCache, savedFeature, resourcesDir)
            resolvedSavedFeature = warningService.featureWithWarnings(resolvedSavedFeature)


            val oldPath = featureToSave.oldPath
            val newPath = resolvedSavedFeature.path

            if (oldPath != null && oldPath != newPath) {
                val newFeaturesByPath = HashMap<Path, Feature>()

                for ((featurePath, feature) in featuresByPath) {
                    val newFeaturePath = featurePath.replaceDirs(oldPath, newPath)

                    // todo: make "Feature.name" a calculated property
                    newFeaturesByPath[newFeaturePath] = feature.copy(
                        name = newFeaturePath.directories.lastOrNull().orEmpty(),
                        path = newFeaturePath,
                        oldPath = newFeaturePath
                    )
                }

                featuresByPath = newFeaturesByPath
            } else {
                featuresByPath[resolvedSavedFeature.path] = resolvedSavedFeature
            }

            return savedFeature
        }
    }

    fun deleteFeatureAndAttachments(path: Path) {
        lock.write {
            val featuresDir = this.featuresDir
                ?: throw IllegalStateException("cannot save composed step because the featuresDir is not set")
            val resourcesDir = this.resourcesDir
                ?: throw IllegalStateException("cannot save composed step because the resourcesDir is not set")


            featureFileService.deleteFeature(path, featuresDir)

            initialize(featuresDir, resourcesDir)
        }
    }
}
