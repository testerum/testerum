package com.testerum.file_service.caches.resolved

import com.testerum.file_service.file.FeatureFileService
import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import java.nio.file.Path as JavaPath

class FeaturesCache(private val featureFileService: FeatureFileService) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FeaturesCache::class.java)
    }

    private val lock = ReentrantReadWriteLock()

    private var featuresByPath: MutableMap<Path, Feature> = hashMapOf()

    fun initialize(featuresDir: JavaPath) {
        lock.write {
            val startTimeMillis = System.currentTimeMillis()

            val newFeaturesByPath = ConcurrentHashMap<Path, Feature>()

            val features = featureFileService.getAllFeatures(featuresDir)
            for (feature in features) {
                val pathWithoutFile = feature.path.withoutFile()

                val featureWithoutFile = feature.copy(
                        path = pathWithoutFile,
                        oldPath = pathWithoutFile
                )

                newFeaturesByPath[featureWithoutFile.path] = featureWithoutFile
            }

            featuresByPath = newFeaturesByPath

            val endTimeMillis = System.currentTimeMillis()

            LOG.info("loading ${newFeaturesByPath.size} features took ${endTimeMillis - startTimeMillis} ms")
        }
    }

    fun getAllFeatures(): Collection<Feature> = lock.read { featuresByPath.values }

    fun getFeatureAtPath(path: Path): Feature? = lock.read { featuresByPath[path] }

    fun save(featureToSave: Feature, featuresDir: JavaPath): Feature {
        lock.write {
            val savedFeature = featureFileService.save(featureToSave, featuresDir)

            val oldPath = featureToSave.oldPath
            val newPath = savedFeature.path

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
                featuresByPath[savedFeature.path] = savedFeature
            }

            return savedFeature
        }
    }

    fun deleteFeatureAndAttachments(path: Path, featuresDir: JavaPath) {
        lock.write {
            featureFileService.deleteFeature(path, featuresDir)

            initialize(featuresDir)
        }
    }

}
