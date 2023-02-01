package com.testerum.common.json_diff

import com.testerum.common.json_diff.impl.node_comparer.JsonCompareResult

interface JsonComparer {

    fun compare(expectedJson: String, actualJson: String): JsonCompareResult

}
