package com.testerum.web_backend.module_di

import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import java.util.*

class WebBackendModuleBootstrapperSpringIntegration : ApplicationContextAware, BeanFactoryPostProcessor, DisposableBean {

    private val bootstrapper = WebBackendModuleBootstrapper()

    private lateinit var applicationContext: ApplicationContext

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        registerWebControllers(beanFactory)
        registerWebSocketController(beanFactory)
        registerJsonObjectMapper(beanFactory)
    }

    private fun registerWebControllers(beanFactory: ConfigurableListableBeanFactory) {
        val controllers = bootstrapper.webBackendModuleFactory.webControllers

        emulateSpringInterfaces(controllers)

        for (controller in controllers) {
            val beanName = getBeanName(controller)
            beanFactory.registerSingleton(beanName, controller)
        }
    }

    private fun emulateSpringInterfaces(controllers: List<Any>) {
        for (controller in controllers) {
            if (controller is ApplicationContextAware) {
                controller.setApplicationContext(applicationContext)
            }

            if (controller is InitializingBean) {
                controller.afterPropertiesSet()
            }
        }
    }

    private fun registerWebSocketController(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.registerSingleton("testsWebSocketController", bootstrapper.webBackendModuleFactory.testsWebSocketController)
    }

    private fun registerJsonObjectMapper(beanFactory: ConfigurableListableBeanFactory) {
        beanFactory.registerSingleton(
                "jsonObjectMapper",
                bootstrapper.webBackendModuleFactory.restApiObjectMapper
        )
    }

    private fun getBeanName(beanInstance: Any): String {
        val qualifier: Qualifier? = beanInstance.javaClass.getDeclaredAnnotation(Qualifier::class.java)

        if (qualifier != null) {
            return qualifier.value
        } else {
            return "${beanInstance.javaClass.name}::${UUID.randomUUID()}"
        }
    }

    override fun destroy() {
        bootstrapper.context.shutdown()
    }

}
