package com.testerum.web_backend.services.manual

import com.testerum.model.manual.ManualStepCall
import com.testerum.model.manual.ManualTest
import com.testerum.model.manual.enums.ManualTestStatus
import com.testerum.model.manual.enums.ManualTestStepStatus
import com.testerum.model.test.TestModel

class AutomatedToManualTestMapper {

    fun convert(test: TestModel): ManualTest {
        val stepCalls = test.stepCalls.map { stepCall ->
            ManualStepCall(
                    stepCall = stepCall,
                    status = ManualTestStepStatus.NOT_EXECUTED,
                    enabled = stepCall.enabled
            )
        }

        return ManualTest(
                path = test.path,
                oldPath = test.path,
                name = test.name,
                description = test.description,
                tags = test.tags,
                stepCalls = stepCalls,
                status = ManualTestStatus.NOT_EXECUTED,
                comments = null,
                isFinalized = false
        )
    }

}
