package net.qutester.service.resources.handler

import net.qutester.model.resources.ResourceContext

interface ResourceHandler {
    fun handle(resourceContext: ResourceContext): ResourceContext
}