package com.testerum.model.text.parts.param_meta.type

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes(value = [
    JsonSubTypes.Type(value = ObjectTypeMeta::class, name = "OBJECT,"),
    JsonSubTypes.Type(value = StringTypeMeta::class, name = "STRING,"),
    JsonSubTypes.Type(value = BooleanTypeMeta::class, name = "BOOLEAN,"),
    JsonSubTypes.Type(value = NumberTypeMeta::class, name = "NUMBER,"),
    JsonSubTypes.Type(value = DateTypeMeta::class, name = "DATE,"),
    JsonSubTypes.Type(value = EnumTypeMeta::class, name = "ENUM,"),
    JsonSubTypes.Type(value = ListTypeMeta::class, name = "LIST,")
])
interface TypeMeta
