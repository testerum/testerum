package net.testerum.resource_manager

import com.testerum.api.annotations.hooks.AfterEachTest

class TestContext {

    val globalVariables: MutableMap<String, Any> = mutableMapOf()
    val scenarioVariables: MutableMap<String, Any> = mutableMapOf()

    fun addScenarioVariable(key: String, value: Any) {
        scenarioVariables.put(key, value)
    }

    fun removeScenarioVariable(key: String) {
        scenarioVariables.remove(key)
    }

    fun getScenarioVariable(key: String): Any? {
        return scenarioVariables.get(key)
    }

    fun addGlobalVariable(key: String, value: Any) {
        globalVariables.put(key, value)
    }

    fun removeGlobalVariable(key: String) {
        globalVariables.remove(key)
    }

    fun getGlobalVariable(key: String): Any? {
        return globalVariables.get(key)
    }

    @AfterEachTest
    fun afterScenario() {
        scenarioVariables.clear()
    }

}