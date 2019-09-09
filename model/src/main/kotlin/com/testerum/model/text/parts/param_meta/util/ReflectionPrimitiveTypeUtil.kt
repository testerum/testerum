package com.testerum.model.text.parts.param_meta.util

object ReflectionPrimitiveTypeUtil {

    fun isPrimitive(typeName: String?): Boolean {
        if (typeName == null) return false
        if (typeName == "byte") return true
        if (typeName == "short") return true
        if (typeName == "int") return true
        if (typeName == "long") return true
        if (typeName == "char") return true
        if (typeName == "float") return true
        if (typeName == "double") return true
        if (typeName == "boolean") return true
        return false
    }
    fun isPrimitiveNumber(typeName: String?): Boolean {
        if (typeName == null) return false
        if (typeName == "byte") return true
        if (typeName == "short") return true
        if (typeName == "int") return true
        if (typeName == "long") return true
        if (typeName == "float") return true
        if (typeName == "double") return true
        return false
    }
    fun isPrimitiveBoolean(typeName: String?): Boolean {
        if (typeName == null) return false
        if (typeName == "boolean") return true
        return false
    }
    fun isPrimitiveChar(typeName: String?): Boolean {
        if (typeName == null) return false
        if (typeName == "char") return true
        return false
    }

    fun getPrimitiveClass(typeName: String): Class<*> {
        if (typeName == "byte") return Byte::class.java
        if (typeName == "short") return Short::class.java
        if (typeName == "int") return Int::class.java
        if (typeName == "long") return Long::class.java
        if (typeName == "char") return Char::class.java
        if (typeName == "float") return Float::class.java
        if (typeName == "double") return Double::class.java
        if (typeName == "boolean") return Boolean::class.java
        if (typeName == "void") return Void.TYPE
        throw IllegalArgumentException("Not primitive type : $typeName")
    }

    fun getTypeDefaultValue(typeName: String): String {
        if (typeName == "byte") return "0"
        if (typeName == "short") return "0"
        if (typeName == "int") return "0"
        if (typeName == "long") return "0L"
        if (typeName == "char") return "'\u0000'"
        if (typeName == "float") return "0.0F"
        if (typeName == "double") return "0.0"
        if (typeName == "boolean") return "false"
        if (typeName == "void") return "void"
        throw IllegalArgumentException("Not primitive type : $typeName")
    }
}