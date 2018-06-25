package com.testerum.api.test_context.test_vars

class VariableNotFoundException(val varName: String) : RuntimeException("cannot find variable [$varName]")
