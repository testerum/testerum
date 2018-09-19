package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.feature.Feature
import com.testerum.model.infrastructure.path.Path
import com.testerum.test_file_format.feature.FileFeature
import java.nio.file.Path as JavaPath

class FileToBusinessFeatureMapper {

    fun mapFeature(fileFeature: FileFeature, relativeFilePath: JavaPath): Feature {
        val path = Path.createInstance(relativeFilePath.toString())

        return Feature(
                name = relativeFilePath.parent?.fileName?.toString().orEmpty(),
                path = path,
                oldPath = path,
                description = fileFeature.description,
                tags = fileFeature.tags
        )
    }

}
