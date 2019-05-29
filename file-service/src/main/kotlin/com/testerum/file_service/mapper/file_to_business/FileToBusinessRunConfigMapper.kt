package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.infrastructure.path.Path
import com.testerum.model.runner.config.FileRunnerConfig
import com.testerum.model.runner.config.RunnerConfig
import java.nio.file.Path as JavaPath

class FileToBusinessRunConfigMapper {

    fun mapRunConfig(fileConfig: FileRunnerConfig,
                     relativeFilePath: JavaPath): RunnerConfig {
        val path = Path.createInstance(relativeFilePath.toString())

        return RunnerConfig(
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
