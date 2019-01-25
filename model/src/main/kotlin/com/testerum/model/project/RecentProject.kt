package com.testerum.model.project

import java.time.LocalDateTime
import java.nio.file.Path as JavaPath

data class RecentProject(val path: JavaPath,
                         val lastOpened: LocalDateTime)
