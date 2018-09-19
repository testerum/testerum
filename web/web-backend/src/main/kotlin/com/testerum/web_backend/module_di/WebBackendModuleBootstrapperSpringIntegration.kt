package com.testerum.web_backend.module_di

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.guava.GuavaModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.afterburner.AfterburnerModule
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import java.util.*

class WebBackendModuleBootstrapperSpringIntegration : BeanFactoryPostProcessor, DisposableBean {

    private val bootstrapper = WebBackendModuleBootstrapper()

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        registerWebControllers(beanFactory)
        registerWebSocketController(beanFactory)
        registerJsonObjectMapper(beanFactory)
    }

    private fun registerWebControllers(beanFactory: ConfigurableListableBeanFactory) {
        val controllers = bootstrapper.webBackendModuleFactory.webControllers

        for (controller in controllers) {
            val beanName = "${controller.javaClass.name}::${UUID.randomUUID()}"
            beanFactory.registerSingleton(beanName, controller)
        }
    }

    private fun registerWebSocketController(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.registerSingleton("testsWebSocketController", bootstrapper.webBackendModuleFactory.testsWebSocketController)
    }

    private fun registerJsonObjectMapper(beanFactory: ConfigurableListableBeanFactory) {
        val objectMapper = ObjectMapper().apply {
            registerModule(AfterburnerModule())
            registerModule(JavaTimeModule())
            registerModule(GuavaModule())

            disable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)

            disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }

        beanFactory.registerSingleton(
                "jsonObjectMapper",
                objectMapper
        )
    }

    override fun destroy() {
        bootstrapper.context.shutdown()
    }

}
