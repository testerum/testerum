package com.testerum_api.testerum_steps_api.test_context.test_vars

class VariableNotFoundException(val varName: String) : RuntimeException("cannot find variable [$varName]")
