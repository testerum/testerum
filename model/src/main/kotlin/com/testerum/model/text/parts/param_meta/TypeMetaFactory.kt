package com.testerum.model.text.parts.param_meta

import com.testerum.model.text.parts.param_meta.field.FieldTypeMeta
import com.testerum.model.text.parts.param_meta.util.ReflectionPrimitiveTypeUtil
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
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

    fun getTypeMetaFromJavaType(javaClass: Class<*>, genericsType: Type): TypeMeta {

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
            val genericMetaType = getGenericTypeByIndexIfExists(genericsType, 0) ?: StringTypeMeta()
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

        if ("java.lang.Boolean".equals(javaClass.name)) {
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

            //ignore compiler (kotlin) added fields
            if (field.isSynthetic) {
                continue;
            }

            val fieldTypeMeta = getTypeMetaFromJavaType(field.type, field.genericType)
            fieldsTypeMeta.add(
                    FieldTypeMeta(field.name, fieldTypeMeta)
            )
        }

        return ObjectTypeMeta(javaClass.name, fieldsTypeMeta)
    }

    private fun getGenericTypeByIndexIfExists(genericsType: Type, genericIndex: Int): TypeMeta? {


        if (genericsType is ParameterizedType) {
            val typeArguments = genericsType.actualTypeArguments
            if (typeArguments.size > genericIndex) {
                val typeArg = typeArguments[genericIndex]
                return when (typeArg) {
                    is ParameterizedType -> getTypeMetaFromJavaType(typeArg.rawType as Class<*>, typeArg)
                    is Class<*> -> getTypeMetaFromJavaType(typeArg as Class<*>, typeArg)
                    else -> throw RuntimeException ("Unknown Type $typeArg")
                }
            }
        }
        return null;
    }
}