package com.testerum.web_backend.controller.module_di

import com.testerum.common_json.ObjectMapperFactory
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
        beanFactory.registerSingleton(
                "jsonObjectMapper",
                ObjectMapperFactory.WEB_OBJECT_MAPPER
        )
    }

    override fun destroy() {
        bootstrapper.context.shutdown()
    }

}