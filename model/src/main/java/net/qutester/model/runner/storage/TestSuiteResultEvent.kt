package net.qutester.model.runner.storage

import net.qutester.model.enums.RunnerEventTypeEnum
import net.qutester.model.infrastructure.path.Path
import java.util.*

class TestSuiteResultEvent(var testSuiteResultFilePath: Path, var triggeredTime: Date) {

    var event: RunnerEventTypeEnum? = null
    var log: String? = null
}
