package com.testerum.model.text.parts.param_meta

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = BooleanTypeMeta::class, name = "BOOLEAN"),
    JsonSubTypes.Type(value = DateTypeMeta::class, name = "DATE"),
    JsonSubTypes.Type(value = EnumTypeMeta::class, name = "ENUM"),
    JsonSubTypes.Type(value = InstantTypeMeta::class, name = "INSTANT"),
    JsonSubTypes.Type(value = LocalDateTypeMeta::class, name = "LOCAL_DATE"),
    JsonSubTypes.Type(value = LocalDateTimeTypeMeta::class, name = "LOCAL_DATE_TIME"),
    JsonSubTypes.Type(value = ZonedDateTimeTypeMeta::class, name = "ZONED_DATE_TIME"),
    JsonSubTypes.Type(value = ListTypeMeta::class, name = "LIST"),
    JsonSubTypes.Type(value = MapTypeMeta::class, name = "MAP"),
    JsonSubTypes.Type(value = NumberTypeMeta::class, name = "NUMBER"),
    JsonSubTypes.Type(value = ObjectTypeMeta::class, name = "OBJECT"),
    JsonSubTypes.Type(value = StringTypeMeta::class, name = "TEXT")
])
interface TypeMeta {
    val javaType: String;
    fun fileType(): String;
}
