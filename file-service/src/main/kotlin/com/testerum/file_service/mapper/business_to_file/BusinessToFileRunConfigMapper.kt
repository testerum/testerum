package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig

class BusinessToFileRunConfigMapper {

    fun map(config: RunConfig): FileRunConfig {
        return FileRunConfig(
                name = config.name,
                settings = config.settings,
                tagsToInclude = config.tagsToInclude,
                tagsToExclude = config.tagsToExclude,
                pathsToInclude = config.pathsToInclude.map {
                    it.toString()
                }
        )
    }

}
