package com.testerum.file_service.mapper.file_to_business

import com.testerum.file_service.mapper.file_to_business.common.FileToBusinessStepCallMapper
import com.testerum.model.feature.Feature
import com.testerum.model.feature.hooks.Hooks
import com.testerum.model.infrastructure.path.Path
import com.testerum.test_file_format.feature.FileFeature
import java.nio.file.Path as JavaPath

class FileToBusinessFeatureMapper(private val stepCallMapper: FileToBusinessStepCallMapper) {

    fun mapFeature(fileFeature: FileFeature, relativeFilePath: JavaPath): Feature {
        val path = Path.createInstance(relativeFilePath.toString())

        val beforeAllHooks = stepCallMapper.mapStepCalls(
            fileStepCalls = fileFeature.beforeAllHooks,
            stepCallIdPrefix = path.toString()
        )

        val beforeEachHooks = stepCallMapper.mapStepCalls(
            fileStepCalls = fileFeature.beforeEachHooks,
            stepCallIdPrefix = path.toString()
        )

        val afterEachHooks = stepCallMapper.mapStepCalls(
            fileStepCalls = fileFeature.afterEachHooks,
            stepCallIdPrefix = path.toString()
        )

        val afterAllHooks = stepCallMapper.mapStepCalls(
            fileStepCalls = fileFeature.afterAllHooks,
            stepCallIdPrefix = path.toString()
        )

        return Feature(
                name = relativeFilePath.parent?.fileName?.toString().orEmpty(),
                path = path,
                oldPath = path,
                description = fileFeature.description,
                tags = fileFeature.tags,
                hooks = Hooks(beforeAllHooks, beforeEachHooks, afterEachHooks, afterAllHooks)
        )
    }

}
