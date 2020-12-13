package com.testerum

import com.testerum.runner.junit5.TesterumJunitTestFactory
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.TestFactory
import java.util.Arrays

class JunitTesterumTests {

    @Disabled
    @TestFactory
    fun testerumTestsFactory(): List<DynamicNode> {
        return TesterumJunitTestFactory("tests")
            .packagesToScan(Arrays.asList(
                "debug"
            ))
            .tests
    }
}
