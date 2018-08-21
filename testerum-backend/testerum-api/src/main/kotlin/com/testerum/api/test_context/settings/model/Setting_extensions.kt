package com.testerum.api.test_context.settings.model

import java.nio.file.Path
import java.nio.file.Paths

val Setting.resolvedValueAsPath: Path
    get() = Paths.get(resolvedValue)
