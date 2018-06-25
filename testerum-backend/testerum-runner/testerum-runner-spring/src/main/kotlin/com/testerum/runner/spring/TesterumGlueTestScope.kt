package com.testerum.runner.spring

import org.springframework.beans.factory.ObjectFactory
import org.springframework.beans.factory.config.Scope

object TesterumGlueTestScope : Scope {

    val NAME = "testerum-glue"

    private var conversationCounter: Int = 0

    private val objects = mutableMapOf<String, Any>()
    private val destructionCallbacks = mutableMapOf<String, Runnable>()

    override fun get(name: String, objectFactory: ObjectFactory<*>): Any
            = objects.getOrPut(name) {
                objectFactory.getObject()
            }

    override fun remove(name: String): Any?
            = objects.remove(name)

    override fun getConversationId(): String
            = "testerum-glue-scope-$conversationCounter"

    override fun registerDestructionCallback(name: String, callback: Runnable) {
        destructionCallbacks[name] = callback
    }

    override fun resolveContextualObject(key: String?): Any?
            = null

    fun beforeTest() {
        cleanUp()
        conversationCounter++
    }

    fun afterTest() {
        for (callback in destructionCallbacks.values) {
            callback.run()
        }
        cleanUp()
    }

    private fun cleanUp() {
        objects.clear()
        destructionCallbacks.clear()
    }

}