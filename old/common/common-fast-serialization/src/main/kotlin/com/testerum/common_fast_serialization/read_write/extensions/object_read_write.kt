@file:Suppress("NOTHING_TO_INLINE")

package com.testerum.common_fast_serialization.read_write.extensions

import com.testerum.common_fast_serialization.FastMarshaller
import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput

inline fun <T> FastOutput.writeObject(propName: String, data: T, serializer: FastMarshaller<T>) = serializer.serialize(propName, data, this)

inline fun <T> FastInput.readObject(propName: String, parser: FastMarshaller<T>): T = parser.parse(propName, this)
