package com.testerum.file_service.mapper.business_to_file

import com.testerum.file_service.mapper.business_to_file.common.BusinessToFileStepCallMapper
import com.testerum.model.feature.Feature
import com.testerum.test_file_format.feature.FileFeature

class BusinessToFileFeatureMapper(private val businessToFileStepCallMapper: BusinessToFileStepCallMapper) {

    fun mapFeature(feature: Feature): FileFeature {
        return FileFeature(
                description = feature.description,
                tags = feature.tags,
                beforeAllHooks = businessToFileStepCallMapper.mapStepCalls(feature.hooks.beforeAll),
                beforeEachHooks = businessToFileStepCallMapper.mapStepCalls(feature.hooks.beforeEach),
                afterEachHooks = businessToFileStepCallMapper.mapStepCalls(feature.hooks.afterEach),
                afterAllHooks = businessToFileStepCallMapper.mapStepCalls(feature.hooks.afterAll)
        )
    }
}
