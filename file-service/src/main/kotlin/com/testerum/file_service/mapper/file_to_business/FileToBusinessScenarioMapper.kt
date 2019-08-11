package com.testerum.file_service.mapper.file_to_business

import com.testerum.model.test.scenario.Scenario
import com.testerum.test_file_format.testdef.scenarios.FileScenario

class FileToBusinessScenarioMapper(private val fileToBusinessScenarioParamMapper: FileToBusinessScenarioParamMapper) {

    fun mapScenarios(fileScenarios: List<FileScenario>): List<Scenario> = fileScenarios.map { mapScenario(it) }

    private fun mapScenario(fileScenario: FileScenario): Scenario {
        return Scenario(
                name = fileScenario.name,
                params = fileToBusinessScenarioParamMapper.mapScenarioParams(fileScenario.params),
                enabled = fileScenario.enabled
        )
    }

}
