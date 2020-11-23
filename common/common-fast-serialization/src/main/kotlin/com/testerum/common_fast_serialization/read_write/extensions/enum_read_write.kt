@file:Suppress("NOTHING_TO_INLINE")

package com.testerum.common_fast_serialization.read_write.extensions

import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

inline fun <reified E : Enum<E>> FastInput.readEnum(propName: String): E? {
    val propValue = readString(propName)
        ?: return null

    return enumValueOf<E>(propValue)
}

inline fun <reified E : Enum<E>> FastInput.readRequiredEnum(propName: String): E = readEnum<E>(propName)
    ?: throw IllegalArgumentException("cannot find property [$propName]")

inline fun <E : Enum<E>> FastOutput.writeEnum(propName: String, propValue: E) = writeString(propName, propValue.name)
