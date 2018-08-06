package net.qutester.service.resources.handler

import com.testerum.model.resources.ResourceContext

interface ResourceHandler {
    fun handle(resourceContext: ResourceContext): ResourceContext
}