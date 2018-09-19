package com.testerum.file_service.file.util

import com.testerum.model.resources.ResourceContext

fun ResourceContext.isCreateResource(): Boolean {
    return oldPath == null
}

fun ResourceContext.isUpdateResource(): Boolean {
    return !isCreateResource()
}

/**
 * resource move: update where the path has changed
 */
fun ResourceContext.isRelocateResource(): Boolean {
    return isUpdateResource() && oldPath != path
}
