package com.testerum.file_repository.module_factory

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.testerum.common_di.BaseModuleFactory
import com.testerum.common_di.ModuleFactoryContext
import com.testerum.file_repository.AttachmentFileRepositoryService
import com.testerum.file_repository.FileRepositoryService
import com.testerum.settings.module_factory.SettingsModuleFactory

@Suppress("unused", "LeakingThis")
class FileRepositoryModuleFactory(context: ModuleFactoryContext,
                                  settingsModuleFactory: SettingsModuleFactory) : BaseModuleFactory(context) {

    val fileRepositoryObjectMapper: ObjectMapper = jacksonObjectMapper().apply {
        registerModule(AfterburnerModule())
        registerModule(JavaTimeModule())
        registerModule(GuavaModule())

        setSerializationInclusion(JsonInclude.Include.NON_NULL)
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

        disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }

    val fileRepositoryService = FileRepositoryService(settingsModuleFactory.settingsManager)

    val attachmentFileRepositoryService = AttachmentFileRepositoryService(fileRepositoryService)

}