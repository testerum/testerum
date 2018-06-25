package net.qutester.model.enums

enum class ParamTypeEnum(val mappedClasses: List<String>) {
    TEXT(listOf("char", "java.lang.Character", "java.lang.String", String::class.java.name)),
    NUMBER(listOf(
            "byte",
            "short",
            "int",
            "long",
            "float",
            "double",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double"
    )),
    ENUM(emptyList()),
    BOOLEAN(listOf("boolean", "java.lang.Boolean")),
    BINARY(emptyList()),
    DATE(listOf("java.sql.Date")),
    TIME(listOf("java.sql.Time")),
    TIMESTAMP(listOf("java.sql.Timestamp")),
    UNKNOWN(emptyList());

    companion object {
        private val paramTypeByClassMap: Map<String, ParamTypeEnum> = createParamTypeByClassMap()

        private fun createParamTypeByClassMap(): MutableMap<String, ParamTypeEnum> {
            val result = mutableMapOf<String, ParamTypeEnum>()

            for (paramTypeEnum in values()) {
                for (mappedClass in paramTypeEnum.mappedClasses) {
                    result[mappedClass] = paramTypeEnum
                }
            }

            return result
        }

        fun getByClass(javaClassName: String): ParamTypeEnum? {
            return paramTypeByClassMap[javaClassName]
        }

        fun mapToUi(javaClassName: String): String {
            return paramTypeByClassMap[javaClassName]?.name ?: javaClassName
        }
    }

}

