package com.testerum.runner.spring

import com.testerum_api.testerum_steps_api.services.TesterumServiceLocator
import com.testerum_api.testerum_steps_api.test_context.TestContext
import com.testerum_api.testerum_steps_api.test_context.TestContextAware
import com.testerum.runner.glue_object_factory.GlueObjectFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.beans.factory.support.BeanDefinitionBuilder
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.core.io.UrlResource

class SpringGlueObjectFactory : GlueObjectFactory, TestContextAware {

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(SpringGlueObjectFactory::class.java)
    }

    private lateinit var applicationContext: GenericApplicationContext

    override fun setTestContext(testContext: TestContext) {
        this.applicationContext = createApplicationContext(testContext)
    }

    override fun beforeTest() {
        TesterumGlueTestScope.beforeTest()
    }

    override fun afterTest() {
        TesterumGlueTestScope.afterTest()
    }

    override fun addClass(glueClass: Class<*>) {
        applicationContext.registerBeanDefinition(
                glueClass.name + "@" + Integer.toHexString(glueClass.hashCode()),
                BeanDefinitionBuilder.genericBeanDefinition(glueClass)
                        .setScope(TesterumGlueTestScope.NAME)
                        .beanDefinition
        )
    }

    override fun <T> getInstance(glueClass: Class<out T>): T
            = try {
        applicationContext.getBean(glueClass)
    } catch (e: Exception) {
        throw RuntimeException("failed to get glue instance of type [${glueClass.name}]", e)
    }

    private fun createApplicationContext(testContext: TestContext): GenericApplicationContext {
        val applicationContext = GenericApplicationContext().apply {
            classLoader = testContext.stepsClassLoader
        }

        applicationContext.registerShutdownHook()

        // enable @Autowired
        applicationContext.registerBeanDefinition(
                AutowiredAnnotationBeanPostProcessor::class.java.name,
                BeanDefinitionBuilder.genericBeanDefinition(AutowiredAnnotationBeanPostProcessor::class.java)
                        .beanDefinition
        )

        val beanFactory: ConfigurableListableBeanFactory = applicationContext.beanFactory

        // put API objects into the application context
        beanFactory.registerSingleton("testerum.testContext", TesterumServiceLocator.getTestContext())
        beanFactory.registerSingleton("testerum.testVariables", TesterumServiceLocator.getTestVariables())
        beanFactory.registerSingleton("testerum.settingsManager", TesterumServiceLocator.getSettingsManager())
        beanFactory.registerSingleton("testerum.logger", TesterumServiceLocator.getTesterumLogger())


        // load all testerum.xml files from the "steps" classloader
        val xmlReader = XmlBeanDefinitionReader(applicationContext).apply {
            setValidating(true)
            validationMode = XmlBeanDefinitionReader.VALIDATION_XSD
        }
        for (testerumXmlUrl in testContext.stepsClassLoader.getResources("testerum.xml")) {
            LOG.info("loading bean definitions from [$testerumXmlUrl]")

            xmlReader.loadBeanDefinitions(
                    UrlResource(testerumXmlUrl)
            )
        }

        // register glue scope
        beanFactory.registerScope(TesterumGlueTestScope.NAME, TesterumGlueTestScope)

        // register glue scope
        applicationContext.refresh()

        return applicationContext
    }

}
