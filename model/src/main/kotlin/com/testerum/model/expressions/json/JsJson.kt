package com.testerum.model.expressions.json

import com.fasterxml.jackson.module.kotlin.readValue
import com.testerum.model.expressions.json.util.JSON_STEPS_OBJECT_MAPPER
import com.testerum.model.expressions.json.util.MapMerger
import org.openjdk.nashorn.api.scripting.AbstractJSObject
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.write

class JsJson : AbstractJSObject {

    companion object {
        private val PRETTY_PRINTING_JSON_WRITER = JSON_STEPS_OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
    }

    private val lock = ReentrantReadWriteLock()

    @Suppress("MemberVisibilityCanBePrivate")
    private val data: LinkedHashMap<String, Any?>

    private var serialized: String? = null

    constructor(unparsedJson: String) : super() {
        this.data = LinkedHashMap(
                try {
                    JSON_STEPS_OBJECT_MAPPER.readValue<Map<String, Any?>>(unparsedJson)
                } catch (e: Exception) {
                    throw IllegalArgumentException("invalid JSON: [$unparsedJson]", e)
                }
        )
        this.serialized = unparsedJson
    }

    private constructor(data: LinkedHashMap<String, Any?>) : super() {
        this.data = data
    }

    override fun setMember(name: String, value: Any?) {
        lock.write {
            data[name] = value
            serialized = null
        }
    }

    override fun getMember(name: String): Any? {
        return data[name]
    }

    override fun toString(): String {
        lock.write {
            if (serialized == null) {
                serialized = JSON_STEPS_OBJECT_MAPPER.writeValueAsString(this.data)
            }

            return serialized!!
        }
    }

    fun toPrettyString(): String = PRETTY_PRINTING_JSON_WRITER.writeValueAsString(this.data)

    fun overrideWith(overrides: JsJson): JsJson {
        return JsJson(
                MapMerger.override(
                        base = this.data,
                        overrides = overrides.data
                )
        )
    }

}
