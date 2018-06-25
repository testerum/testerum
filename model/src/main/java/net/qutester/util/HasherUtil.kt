package net.qutester.util

import com.google.common.hash.HashCode
import com.google.common.hash.Hashing

fun hashStringAsId(hash: String): Long {
    val hf = Hashing.murmur3_128()
    val hc: HashCode = hf.newHasher().putString(hash, Charsets.UTF_8).hash()
    return hc.asLong()
}