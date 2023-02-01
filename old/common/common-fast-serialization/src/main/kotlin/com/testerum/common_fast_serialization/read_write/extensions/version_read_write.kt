@file:Suppress("NOTHING_TO_INLINE")

package com.testerum.common_fast_serialization.read_write.extensions

import com.testerum.common_fast_serialization.read_write.FastInput
import com.testerum.common_fast_serialization.read_write.FastOutput


inline fun FastOutput.writeVersion(prefix: String, version: Int) = writeInt("$prefix.version", version)

inline fun FastInput.readVersion(prefix: String): Int = readRequiredInt("$prefix.version")

/**
 * @return the actual version (the same as ``expectedVersion`` in this case)
 */
inline fun FastInput.requireExactVersion(prefix: String, expectedVersion: Int): Int {
    val actualVersion = readVersion(prefix)

    if (actualVersion != expectedVersion) {
        throw RuntimeException("only version $expectedVersion is supported")
    }

    return actualVersion
}

/**
 * @return the actual version
 */
inline fun FastInput.requireVersionRange(
    prefix: String,
    minInclusive: Int,
    maxExclusive: Int
): Int {
    val actualVersion = readVersion(prefix)

    if (actualVersion < minInclusive || actualVersion >= maxExclusive) {
        throw RuntimeException("version out of range; wanted [$minInclusive..$maxExclusive), but got $actualVersion")
    }

    return actualVersion
}
