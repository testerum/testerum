package com.testerum.web_backend.filter.project

import java.nio.file.Path as JavaPath

object ProjectDirHolder {

    private val projectRootDir = ThreadLocal<JavaPath>()
    private val currentRequestUri = ThreadLocal<String>()

    fun set(projectPath: JavaPath?,
            currentRequestUri: String?) {
        this.projectRootDir.set(projectPath)
        this.currentRequestUri.set(currentRequestUri)
    }

    fun clear() {
        projectRootDir.remove()
        currentRequestUri.remove()
    }

    fun get(): JavaPath = projectRootDir.get() ?: throw MissingProjectHttpHeaderException("cannot lookup current project: missing HTTP request header [${CurrentProjectFilter.PROJECT_PATH_HEADER_NAME}]; requestUri=[${currentRequestUri.get()}]")

}
