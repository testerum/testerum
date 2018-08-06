package com.testerum.runner_cmdline.transformer.builtin.lang

import com.testerum.api.transformer.ParameterInfo
import com.testerum.api.transformer.Transformer

object CharacterTransformer : Transformer<Char> {

    override fun canTransform(paramInfo: ParameterInfo): Boolean
            = (paramInfo.type == java.lang.Character::class.java) || (paramInfo.type == java.lang.Character.TYPE)

    override fun transform(toTransform: String, paramInfo: ParameterInfo): Char {
        if (toTransform.length != 1) {
            throw IllegalArgumentException("[$toTransform] is not a valid character: it should have exactly 1 character, but it has ${toTransform.length}")
        }

        return toTransform[0]
    }

}