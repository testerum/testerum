package com.testerum.runner_cmdline.transformer.builtin.enums

import com.testerum_api.testerum_steps_api.transformer.ParameterInfo
import com.testerum_api.testerum_steps_api.transformer.Transformer

object EnumTransformer : Transformer<Enum<*>> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = Enum::class.java.isAssignableFrom(paramInfo.type)

    // todo: performance tip: cache enum constants; key=paramInfo.type
    override fun transform(toTransform: String, paramInfo: ParameterInfo): Enum<*> {
        val enumConstants: Set<Enum<*>> = paramInfo.type.enumConstantsSet

        return enumConstants.find { it.name == toTransform }
                ?: throw IllegalArgumentException("[$toTransform] is not a valid enum value for [${paramInfo.type.name}]; valid values are: ${enumConstants.map { it.name }}")
    }

    private val Class<*>.enumConstantsSet: Set<Enum<*>>
        get() {
            val enumConstants = mutableSetOf<Enum<*>>()

            getEnumConstants().mapTo(enumConstants) { it as Enum<*> }

            return enumConstants
        }

}
