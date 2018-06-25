package com.testerum.common.json_diff.impl.node_comparer

import com.testerum.common.json_diff.json_path.JsonPath

sealed class JsonCompareResult

object EqualJsonCompareResult : JsonCompareResult()
class DifferentJsonCompareResult(val message: String, val jsonPath: JsonPath) : JsonCompareResult()
