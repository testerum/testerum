package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.feature.Feature
import com.testerum.test_file_format.feature.FileFeature

class BusinessToFileFeatureMapper {

    fun mapFeature(feature: Feature): FileFeature {
        return FileFeature(
                description = feature.description,
                tags = feature.tags
        )
    }

}
