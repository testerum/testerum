package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.test.scenario.param.ScenarioParam
import com.testerum.model.test.scenario.param.ScenarioParamType
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParam
import com.testerum.test_file_format.testdef.scenarios.FileScenarioParamType

class BusinessToFileScenarioParamMapper {

    fun mapParams(params: List<ScenarioParam>): List<FileScenarioParam> = params.map { mapParam(it) }

    private fun mapParam(param: ScenarioParam): FileScenarioParam {
        return FileScenarioParam(
                name = param.name,
                type = mapParamType(param.type),
                value = param.value
        )
    }

    private fun mapParamType(type: ScenarioParamType): FileScenarioParamType {
        return when (type) {
            ScenarioParamType.TEXT -> FileScenarioParamType.TEXT
            ScenarioParamType.JSON -> FileScenarioParamType.JSON
        }
    }

}
