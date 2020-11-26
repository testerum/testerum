@file:Suppress("NOTHING_TO_INLINE")

package com.testerum.common_fast_serialization.read_write.extensions

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

inline fun <T> FastOutput.writeList(propName: String, list: List<T>, serializer: FastMarshaller<T>) {
    writeInt("$propName.size", list.size)
    for ((i, item) in list.withIndex()) {
        serializer.serialize("$propName[$i]", item,  this)
    }
}

inline fun <T> FastInput.readList(propName: String, parser: FastMarshaller<T>): List<T> {
    val result = mutableListOf<T>()

    val size = readRequiredInt("$propName.size")

    for (i in 0 until size) {
        result += parser.parse("$propName[$i]", this)
    }

    return result
}
