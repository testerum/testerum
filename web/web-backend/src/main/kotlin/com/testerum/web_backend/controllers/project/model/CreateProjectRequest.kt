package com.testerum.web_backend.controllers.project.model

import java.nio.file.Path as JavaPath

data class CreateProjectRequest(val projectParentDir: String,
                                val projectName: String)
