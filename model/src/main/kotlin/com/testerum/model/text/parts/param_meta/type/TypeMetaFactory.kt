package com.testerum.model.text.parts.param_meta.type

import kotlin.reflect.KClass

object TypeMetaFactory {

    val TYPE_META_TO_STRING_MAPPING = mapOf(
        StringTypeMeta::class  to "String",
        BooleanTypeMeta::class to "Boolean",
        NumberTypeMeta::class  to "Number",
        DateTypeMeta::class    to "Date",
        EnumTypeMeta::class    to "Enum",
        ListTypeMeta::class    to "List",
        ObjectTypeMeta::class  to "Object"
    );

    val JAVA_TYPE_TO_STRING_TYPE_META: Map<String, String> = mapOf (
        "TEXT"                to "String",
        "char"                to "String",
        "java.lang.Character" to "String",
        "java.lang.String"    to "String",

        "NUMBER"              to "Number",
        "byte"                to "Number",
        "short"               to "Number",
        "int"                 to "Number",
        "long"                to "Number",
        "float"               to "Number",
        "double"              to "Number",
        "java.lang.Byte"      to "Number",
        "java.lang.Short"     to "Number",
        "java.lang.Integer"   to "Number",
        "java.lang.Long"      to "Number",
        "java.lang.Float"     to "Number",
        "java.lang.Double"    to "Number",

        "ENUM"                to "Enum",

        "BOOLEAN"             to "Boolean",
        "boolean"             to "Boolean",
        "java.lang.Boolean"   to "Boolean",

        "database.relational.connection_manager.model.RdbmsConnection" to "database.relational.connection_manager.model.RdbmsConnection",
        "database.relational.model.RdbmsSql"                           to "database.relational.model.RdbmsSql",
        "database.relational.model.RdbmsVerify"                        to "database.relational.model.RdbmsVerify",
        "com.testerum.model.resources.http.request.HttpRequest"        to "com.testerum.model.resources.http.request.HttpRequest",
        "http.response.verify.model.HttpResponseVerify"                to "http.response.verify.model.HttpResponseVerify",
        "com.testerum.model.resources.http.mock.server.HttpMockServer" to "com.testerum.model.resources.http.mock.server.HttpMockServer",
        "com.testerum.model.resources.http.mock.stub.HttpMock"         to "com.testerum.model.resources.http.mock.stub.HttpMock",
        "json.model.JsonResource"                                      to "json.model.JsonResource"
    );

    fun getTypeMetaFromString(type: String): TypeMeta? {
        val mapping = TYPE_META_TO_STRING_MAPPING.entries.find { entry: Map.Entry<KClass<out Any>, String> -> entry.value == type }
        val metaClass = mapping?.key ?: StringTypeMeta::class
        when (metaClass) {
            StringTypeMeta::class  -> return StringTypeMeta();
            BooleanTypeMeta::class -> return BooleanTypeMeta();
            NumberTypeMeta::class  -> return NumberTypeMeta();
            DateTypeMeta::class    -> return DateTypeMeta();
            EnumTypeMeta::class    -> return EnumTypeMeta("Enum");
            ListTypeMeta::class    -> return ListTypeMeta(StringTypeMeta());
            ObjectTypeMeta::class  -> return ObjectTypeMeta("java.lang.String");
        }
        return null;
    }

    fun getStringTypeFromTypeMeta(typeMeta: TypeMeta): String {
        val typeMetaAsString = TYPE_META_TO_STRING_MAPPING.get(typeMeta::class)
        return typeMetaAsString ?: TYPE_META_TO_STRING_MAPPING.get(StringTypeMeta::class)!!
    }

    fun getTypeMetaFromJavaType(javaTypeAsString: String, enumValues: List<String>): TypeMeta {
        val typeMetaAsString = JAVA_TYPE_TO_STRING_TYPE_META.get(javaTypeAsString) ?: return ObjectTypeMeta(javaTypeAsString)

        val typeMeta = getTypeMetaFromString(typeMetaAsString) ?: return ObjectTypeMeta(javaTypeAsString)

        if (typeMeta is ObjectTypeMeta) {
            return ObjectTypeMeta(javaTypeAsString)
        }

        if (typeMeta is EnumTypeMeta) {
            return EnumTypeMeta(javaTypeAsString, enumValues)
        }
        return typeMeta;
    }
}