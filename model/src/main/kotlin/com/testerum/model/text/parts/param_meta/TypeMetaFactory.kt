package com.testerum.model.text.parts.param_meta

import kotlin.reflect.KClass

object TypeMetaFactory {

    val TYPE_META_TO_STRING_MAPPING = mapOf(
        StringTypeMeta::class  to "TEXT",
        BooleanTypeMeta::class to "BOOLEAN",
        NumberTypeMeta::class  to "NUMBER",
        DateTypeMeta::class    to "DATE",
        EnumTypeMeta::class    to "ENUM",
        ListTypeMeta::class    to "LIST",
        ObjectTypeMeta::class  to "OBJECT"
    );

    val JAVA_TYPE_TO_STRING_TYPE_META: Map<String, String> = mapOf (
        "TEXT"                to "TEXT",
        "char"                to "TEXT",
        "java.lang.Character" to "TEXT",
        "java.lang.String"    to "TEXT",

        "NUMBER"              to "NUMBER",
        "byte"                to "NUMBER",
        "short"               to "NUMBER",
        "int"                 to "NUMBER",
        "long"                to "NUMBER",
        "float"               to "NUMBER",
        "double"              to "NUMBER",
        "java.lang.Byte"      to "NUMBER",
        "java.lang.Short"     to "NUMBER",
        "java.lang.Integer"   to "NUMBER",
        "java.lang.Long"      to "NUMBER",
        "java.lang.Float"     to "NUMBER",
        "java.lang.Double"    to "NUMBER",

        "ENUM"                to "ENUM",

        "BOOLEAN"             to "BOOLEAN",
        "boolean"             to "BOOLEAN",
        "java.lang.Boolean"   to "BOOLEAN",

        "java.util.Date" to "DATE",

        "java.util.ArrayList"     to "LIST",
        "java.util.LinkedList"    to "LIST",
        "java.util.Vector"        to "LIST",
        "java.util.Stack"         to "LIST",
        "java.util.Queue"         to "LIST",
        "java.util.PriorityQueue" to "LIST",
        "java.util.ArrayDeque"    to "LIST",
        "java.util.HashSet"       to "LIST",
        "java.util.LinkedHashSet" to "LIST",
        "java.util.TreeSet"       to "LIST",
        "java.util.SortedSet"     to "LIST"

    );

    fun getTypeMetaFromString(type: String?): TypeMeta? {
        if (type == null) return null

        val mapping = TYPE_META_TO_STRING_MAPPING.entries.find { entry: Map.Entry<KClass<out Any>, String> -> entry.value == type }
        val metaClass = mapping?.key ?: StringTypeMeta::class
        when (metaClass) {
            StringTypeMeta::class -> return StringTypeMeta();
            BooleanTypeMeta::class -> return BooleanTypeMeta();
            NumberTypeMeta::class -> return NumberTypeMeta("java.lang.Integer");
            DateTypeMeta::class -> return DateTypeMeta();
            EnumTypeMeta::class -> return EnumTypeMeta("Enum");
            ListTypeMeta::class -> return ListTypeMeta("java.util.ArrayList", StringTypeMeta());
            ObjectTypeMeta::class -> return ObjectTypeMeta("java.lang.String");
        }
        return null;
    }

    fun getStringTypeFromTypeMeta(typeMeta: TypeMeta): String {
        val typeMetaAsString = TYPE_META_TO_STRING_MAPPING.get(typeMeta::class)
        return typeMetaAsString ?: TYPE_META_TO_STRING_MAPPING.get(StringTypeMeta::class)!!
    }

    fun getTypeMetaFromJavaType(javaTypeAsString: String, enumValues: List<String>): TypeMeta {
        val typeMetaAsString = JAVA_TYPE_TO_STRING_TYPE_META.get(javaTypeAsString) ?: return ObjectTypeMeta(javaTypeAsString)

        val typeMeta = getTypeMetaFromString(typeMetaAsString)
                ?: return ObjectTypeMeta(javaTypeAsString)

        if (typeMeta is ObjectTypeMeta) {
            return ObjectTypeMeta(javaTypeAsString)
        }

        if (typeMeta is EnumTypeMeta) {
            return EnumTypeMeta(javaTypeAsString, enumValues)
        }
        return typeMeta;
    }
}