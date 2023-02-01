package com.testerum.file_service.mapper.business_to_file

import com.testerum.model.test.scenario.Scenario
import com.testerum.test_file_format.testdef.scenarios.FileScenario

class BusinessToFileScenarioMapper(private val businessToFileScenarioParamMapper: BusinessToFileScenarioParamMapper) {

    fun mapScenarios(scenarios: List<Scenario>): List<FileScenario> = scenarios.map { mapScenario(it) }

    fun mapScenario(scenario: Scenario): FileScenario {
        return FileScenario(
                name = scenario.name,
                params = businessToFileScenarioParamMapper.mapParams(scenario.params),
                enabled = scenario.enabled
        )
    }

}
