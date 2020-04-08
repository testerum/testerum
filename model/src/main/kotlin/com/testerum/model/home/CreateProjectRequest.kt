package com.testerum.model.home

import java.nio.file.Path as JavaPath

data class CreateProjectRequest(val projectParentDir: String,
                                val projectName: String)
