@file:Suppress("NOTHING_TO_INLINE")

package com.testerum.common_fast_serialization.read_write.extensions

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

inline fun <T> FastOutput.writeNullable(prefix: String, nullable: T?, serializer: FastMarshaller<T>) {
    writeBoolean("$prefix.isNullable", nullable == null)
    nullable?.let { serializer.serialize(prefix, it, this) }
}

inline fun <T> FastInput.readNullable(prefix: String, parser: FastMarshaller<T>): T? {
    val isNull = readRequiredBoolean("$prefix.isNullable")

    return if (isNull) {
        null
    } else {
        parser.parse(prefix, this)
    }
}
