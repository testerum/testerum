package com.testerum.common.json_diff

import com.testerum.common.json_diff.json_path.JsonPath

class JsonAssertionError(message: String, val jsonPath: JsonPath) : AssertionError(message)
