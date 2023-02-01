package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.test.scenario.param.ScenarioParam
import com.testerum.model.test.scenario.param.ScenarioParamType
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParam
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParamType

class FileToBusinessScenarioParamMapper {

    fun mapScenarioParams(fileParams: List<FileScenarioParam>): List<ScenarioParam> = fileParams.map { mapScenarioParam(it) }

    private fun mapScenarioParam(fileScenarioParam: FileScenarioParam): ScenarioParam {
        return ScenarioParam(
                name = fileScenarioParam.name,
                type = mapScenarioParamType(fileScenarioParam.type),
                value = fileScenarioParam.value
        )
    }

    private fun mapScenarioParamType(fileScenarioParamType: FileScenarioParamType): ScenarioParamType {
        return when (fileScenarioParamType) {
            FileScenarioParamType.TEXT -> ScenarioParamType.TEXT
            FileScenarioParamType.JSON -> ScenarioParamType.JSON
        }
    }

}
