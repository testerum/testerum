package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.runner.config.FileRunConfig
import com.testerum.model.runner.config.RunConfig

class BusinessToFileRunConfigMapper {

    fun mapRunConfig(config: RunConfig): FileRunConfig {
        return FileRunConfig(
                name = config.name,
                settings = config.settings,
                tagsToInclude = config.tagsToInclude,
                tagsToExclude = config.tagsToExclude,
                paths = config.paths.map {
                    it.toString()
                }
        )
    }

}
