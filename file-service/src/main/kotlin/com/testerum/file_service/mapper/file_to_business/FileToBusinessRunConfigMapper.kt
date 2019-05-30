package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig
import java.nio.file.Path as JavaPath

class FileToBusinessRunConfigMapper {

    fun map(fileConfig: FileRunConfig): RunConfig {
        return RunConfig(
                name = fileConfig.name,
                settings = fileConfig.settings,
                tagsToInclude = fileConfig.tagsToInclude,
                tagsToExclude = fileConfig.tagsToExclude,
                pathsToInclude = fileConfig.pathsToInclude.map {
                    Path.createInstance(it)
                }
        )
    }

}
