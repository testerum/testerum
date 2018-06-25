package com.testerum.runner.object_factory

import com.testerum.runner.glue_object_factory.GlueObjectFactory
import java.lang.reflect.Constructor

class DefaultGlueObjectFactory : GlueObjectFactory {

    private val instances = hashMapOf<Class<*>, Any>()

    override fun beforeTest() { }

    override fun afterTest() {
        instances.clear()
    }

    override fun addClass(glueClass: Class<*>) {
        // nothing to do: for performance reasons, instances are added the first time the "getInstance()" method is called
    }

    override fun <T> getInstance(glueClass: Class<out T>): T {
        val instance: Any = instances.getOrPut(glueClass) {
            createInstance(glueClass) as Any
        }

        return glueClass.cast(instance)
    }

    private fun <T> createInstance(type: Class<out T>): T {
        if (type.isPrimitive) {
            throw IllegalArgumentException("[${type.name}] is a primitive and cannot be instantiated")
        }

        val constructor: Constructor<out T> = try {
            type.getConstructor()
        } catch (e: NoSuchMethodException) {
            throw RuntimeException("class [${type.name}] doesn't have a no-args constructor. If you need dependency-injection add a GlueObjectFactory implementation to the classpath (e.g. testerum-runner-spring).", e)
        }

        try {
            val instance = constructor.newInstance()

            return instance
        } catch (e: Exception) {
            throw RuntimeException("failed to instantiate [${type.name}]", e)
        }
    }

}