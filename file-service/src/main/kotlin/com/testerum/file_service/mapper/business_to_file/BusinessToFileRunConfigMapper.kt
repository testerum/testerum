package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.runner.config.FileRunnerConfig
import com.testerum.model.runner.config.RunnerConfig

class BusinessToFileRunConfigMapper {

    fun mapRunConfig(config: RunnerConfig): FileRunnerConfig {
        return FileRunnerConfig(
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
