package json.model

import com.fasterxml.jackson.module.kotlin.readValue
import jdk.nashorn.api.scripting.AbstractJSObject
import json.utils.JSON_STEPS_OBJECT_MAPPER
import java.util.TreeMap

class JsJson : AbstractJSObject {

    private val unparsedJson: String

    @Suppress("MemberVisibilityCanBePrivate")
    val data: TreeMap<String, Any?>

    private constructor(unparsedJson: String,
                        data: TreeMap<String, Any?>) : super() {
        this.unparsedJson = unparsedJson
        this.data = data
    }

    constructor(unparsedJson: String) : super() {
        this.unparsedJson = unparsedJson
        this.data = TreeMap(
                try {
                    JSON_STEPS_OBJECT_MAPPER.readValue<Map<String, Any?>>(unparsedJson)
                } catch (e: Exception) {
                    throw IllegalArgumentException("invalid JSON: [$unparsedJson]", e)
                }
        )
    }

    override fun setMember(name: String, value: Any?) {
        data[name] = value
    }

    override fun getMember(name: String): Any? {
        return data[name]
    }

    override fun toString(): String {
        return unparsedJson
    }

    fun overrideWith(overrides: JsJson) {

    }

}
