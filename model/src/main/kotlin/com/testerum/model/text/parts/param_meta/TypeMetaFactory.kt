package com.testerum.model.text.parts.param_meta

import com.testerum.model.text.parts.param_meta.field.FieldTypeMeta
import com.testerum.model.text.parts.param_meta.util.ReflectionPrimitiveTypeUtil
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*
import kotlin.reflect.KClass


object TypeMetaFactory {

    val TYPE_META_TO_STRING_MAPPING = mapOf(
        StringTypeMeta::class        to "TEXT",
        BooleanTypeMeta::class       to "BOOLEAN",
        NumberTypeMeta::class        to "NUMBER",
        DateTypeMeta::class          to "DATE",
        InstantTypeMeta::class       to "INSTANT",
        LocalDateTypeMeta::class     to "LOCAL_DATE",
        LocalDateTimeTypeMeta::class to "LOCAL_DATE_TIME",
        ZonedDateTimeTypeMeta::class to "ZONED_DATE_TIME",
        EnumTypeMeta::class          to "ENUM",
        ListTypeMeta::class          to "LIST",
        ObjectTypeMeta::class        to "OBJECT"
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
            InstantTypeMeta::class -> return InstantTypeMeta();
            LocalDateTypeMeta::class -> return LocalDateTypeMeta();
            LocalDateTimeTypeMeta::class -> return LocalDateTimeTypeMeta();
            ZonedDateTimeTypeMeta::class -> return ZonedDateTimeTypeMeta();
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
            val keyGenericMetaType = getGenericTypeByIndexIfExists(genericsType, 0) ?: StringTypeMeta()
            val valueGenericMetaType = getGenericTypeByIndexIfExists(genericsType, 1) ?: StringTypeMeta()

            if (keyGenericMetaType !is StringTypeMeta) throw java.lang.RuntimeException("Only Maps with String key are allowed as parameters or innerparameters for steps. Map type with problem: $genericsType")

            return MapTypeMeta(javaClass.name, keyGenericMetaType, valueGenericMetaType)
        }

        if (Number::class.java.isAssignableFrom(javaClass)) {
            return NumberTypeMeta(javaClass.name)
        }

        if ("java.lang.Character".equals(javaClass.name) || String::class.java.isAssignableFrom(javaClass)) {
            return StringTypeMeta(javaClass.name)
        }

        if ("java.lang.Boolean".equals(javaClass.name)) {
            return BooleanTypeMeta(javaClass.name)
        }

        if (Date::class.java.isAssignableFrom(javaClass)) {
            return DateTypeMeta(javaClass.name)
        }
        if (Instant::class.java.isAssignableFrom(javaClass)) {
            return InstantTypeMeta(javaClass.name)
        }
        if (LocalDate::class.java.isAssignableFrom(javaClass)) {
            return InstantTypeMeta(javaClass.name)
        }
        if (LocalDateTime::class.java.isAssignableFrom(javaClass)) {
            return InstantTypeMeta(javaClass.name)
        }
        if (ZonedDateTime::class.java.isAssignableFrom(javaClass)) {
            return InstantTypeMeta(javaClass.name)
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

            //ignore static fields
            if (Modifier.isStatic(field.modifiers)) {
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
                    is Class<*> -> getTypeMetaFromJavaType(typeArg, typeArg)
                    is ParameterizedType -> getTypeMetaFromJavaType(typeArg.rawType as Class<*>, typeArg)

                    is WildcardType -> {
                        val upperBounds = typeArg.upperBounds
                        if(upperBounds.size > 1) throw java.lang.RuntimeException("Generics with multiple upper bounds are not supported: $typeArg")

                        val upperWildcardType = upperBounds[0]
                        when (upperWildcardType) {
                            is Class<*> -> getTypeMetaFromJavaType(upperWildcardType, typeArg)
                            is ParameterizedType -> getTypeMetaFromJavaType(upperWildcardType.rawType as Class<*>, typeArg)
                            else -> throw java.lang.RuntimeException("Generics with complex upper bounds are not supported: $typeArg")
                        }
                        return getGenericTypeByIndexIfExists(upperBounds[0], 0);
                    }
                    else -> throw RuntimeException ("Unknown Type $typeArg")
                }
            }
        }
        return null;
    }
}