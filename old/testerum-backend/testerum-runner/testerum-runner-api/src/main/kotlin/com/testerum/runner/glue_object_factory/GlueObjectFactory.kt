package com.testerum.runner.glue_object_factory

interface GlueObjectFactory {

    fun beforeTest()

    fun afterTest()

    fun addClass(glueClass: Class<*>)

    fun <T> getInstance(glueClass: Class<out T>): T
    
}