package com.testerum_api.testerum_steps_api.test_context.settings.model

import java.nio.file.Paths
import java.nio.file.Path as JavaPath

val Setting.resolvedValueAsPath: JavaPath
    get() = Paths.get(resolvedValue)
