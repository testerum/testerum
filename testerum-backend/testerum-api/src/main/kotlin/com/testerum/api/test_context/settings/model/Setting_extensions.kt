package com.testerum.api.test_context.settings.model

import java.nio.file.Paths
import java.nio.file.Path as JavaPath

val Setting.resolvedValueAsPath: JavaPath
    get() = Paths.get(resolvedValue)
