package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig
import java.nio.file.Path as JavaPath

class FileToBusinessRunConfigMapper {

    fun mapRunConfig(fileConfig: FileRunConfig,
                     relativeFilePath: JavaPath): RunConfig {
        val path = Path.createInstance(relativeFilePath.toString())

        return RunConfig(
                name = fileConfig.name,
                path = path,
                oldPath = path,
                settings = fileConfig.settings,
                tagsToInclude = fileConfig.tagsToInclude,
                tagsToExclude = fileConfig.tagsToExclude,
                paths = fileConfig.paths.map {
                    Path.createInstance(it)
                }
        )
    }

}
