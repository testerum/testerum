package com.testerum.model.variable

import java.util.*

data class FileProjectLocalVariables(val currentEnvironment: String,
                                     val localVariables: TreeMap<String, String>)
