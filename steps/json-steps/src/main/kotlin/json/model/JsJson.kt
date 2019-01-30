package json.model

import com.fasterxml.jackson.module.kotlin.readValue
import jdk.nashorn.api.scripting.AbstractJSObject
import json.utils.JSON_STEPS_OBJECT_MAPPER
import json.utils.MapMerger

class JsJson : AbstractJSObject {

    @Suppress("MemberVisibilityCanBePrivate")
    val data: LinkedHashMap<String, Any?>

    constructor(unparsedJson: String) : super() {
        this.data = LinkedHashMap(
                try {
                    JSON_STEPS_OBJECT_MAPPER.readValue<Map<String, Any?>>(unparsedJson)
                } catch (e: Exception) {
                    throw IllegalArgumentException("invalid JSON: [$unparsedJson]", e)
                }
        )
    }

    private constructor(data: LinkedHashMap<String, Any?>) : super() {
        this.data = data
    }

    override fun setMember(name: String, value: Any?) {
        data[name] = value
    }

    override fun getMember(name: String): Any? {
        return data[name]
    }

    override fun toString(): String {
        // todo: how to avoid serializing multiple times?
        return JSON_STEPS_OBJECT_MAPPER.writeValueAsString(this.data)
    }

    fun overrideWith(overrides: JsJson): JsJson {
        return JsJson(
                MapMerger.override(
                        base = this.data,
                        overrides = overrides.data
                )
        )
    }

}
