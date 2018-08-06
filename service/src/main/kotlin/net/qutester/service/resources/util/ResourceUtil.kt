package net.qutester.service.resources.util

import com.testerum.model.resources.ResourceContext

fun ResourceContext.isCreateResource(): Boolean {
    return oldPath == null
}

fun ResourceContext.isUpdateResource(): Boolean {
    return !isCreateResource()
}

fun ResourceContext.isRelocateResource(): Boolean {
    return isUpdateResource() && oldPath != path
}
