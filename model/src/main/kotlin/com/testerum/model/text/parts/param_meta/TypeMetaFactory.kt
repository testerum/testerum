package com.testerum.model.text.parts.param_meta

import com.testerum.model.text.parts.param_meta.field.FieldTypeMeta
import com.testerum.model.text.parts.param_meta.util.ReflectionPrimitiveTypeUtil
import java.lang.reflect.ParameterizedType
import java.util.*
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

    fun getTypeMetaFromJavaType(javaClass: Class<*>): TypeMeta {

        if (javaClass.name == "java.lang.Object") {
            return StringTypeMeta(javaClass.name)
        }

        if (ReflectionPrimitiveTypeUtil.isPrimitive(javaClass.name)) {
            val primitiveClass = ReflectionPrimitiveTypeUtil.getPrimitiveClass(javaClass.name)

            if (ReflectionPrimitiveTypeUtil.isPrimitiveNumber(javaClass.name)) {
                return NumberTypeMeta(primitiveClass.name)
            }

            if (ReflectionPrimitiveTypeUtil.isPrimitiveBoolean(javaClass.name)) {
                return BooleanTypeMeta(primitiveClass.name)
            }

            if (ReflectionPrimitiveTypeUtil.isPrimitiveChar(javaClass.name)) {
                return StringTypeMeta(primitiveClass.name)
            }

            return StringTypeMeta(javaClass.name)
        }

        if (Collection::class.java.isAssignableFrom(javaClass)) {
            val genericMetaType = getGenericTypeByIndexIfExists(javaClass, 0) ?: StringTypeMeta()
            return ListTypeMeta(javaClass.name, genericMetaType)
        }

        if (Map::class.java.isAssignableFrom(javaClass)) {
            val keyGenericMetaType = getGenericTypeByIndexIfExists(javaClass, 0) ?: StringTypeMeta()
            val valueGenericMetaType = getGenericTypeByIndexIfExists(javaClass, 1) ?: StringTypeMeta()
            return MapTypeMeta(javaClass.name, keyGenericMetaType, valueGenericMetaType)
        }

        if (Number::class.java.isAssignableFrom(javaClass)) {
            return NumberTypeMeta(javaClass.name)
        }

        if (String::class.java.isAssignableFrom(javaClass)) {
            return StringTypeMeta(javaClass.name)
        }

        if (Boolean::class.java.isAssignableFrom(javaClass)) {
            return BooleanTypeMeta(javaClass.name)
        }

        if (Date::class.java.isAssignableFrom(javaClass)) {
            return DateTypeMeta(javaClass.name)
        }

        if (javaClass.isEnum) {
            val enumValues: List<String>
            val enumConstants: Array<out Any>? = javaClass.enumConstants
            enumValues = enumConstants?.map { (it as Enum<*>).name }
                    ?.toList()
                    ?: Collections.emptyList()

            return EnumTypeMeta(javaClass.name, enumValues)
        }

        val fieldsTypeMeta: MutableList<FieldTypeMeta> = mutableListOf<FieldTypeMeta>()
        for (field in javaClass.declaredFields) {

            //ignore kotlin added fields
            if (field.type.name.startsWith("[Lkotlin.") ||
                    field.type.name.startsWith("kotlin")) {
                continue;
            }

            val fieldTypeMeta = getTypeMetaFromJavaType(field.type)
            fieldsTypeMeta.add(
                    FieldTypeMeta(field.name, fieldTypeMeta)
            )
        }

        return ObjectTypeMeta(javaClass.name, fieldsTypeMeta)
    }

    private fun getGenericTypeByIndexIfExists(javaClass: Class<*>, genericIndex: Int): TypeMeta? {


        if (javaClass is ParameterizedType) {
            val parameterizedType = javaClass as ParameterizedType
            val typeArguments = parameterizedType.actualTypeArguments
            if (typeArguments.size > genericIndex) {
                val typeArgClass = typeArguments[genericIndex] as Class<*>;
                return getTypeMetaFromJavaType(typeArgClass)
            }
        }
        return null;
    }
}