package com.testerum.common_fast_serialization.read_write

import java.io.Closeable
import java.io.DataOutputStream
import java.io.Flushable
import java.io.OutputStream

class FastOutput(
    outputStream: OutputStream
) : Flushable,
    Closeable {

    companion object {
        internal val FILE_FORMAT_SIGNATURE = byteArrayOf(
            'T'.toByte(),  // Testerum
            'F'.toByte(),  // Fast
            'S'.toByte(),  // Serialization
            'F'.toByte()   // Format
        )
        internal const val FILE_FORMAT_VERSION: Int = 1

        internal const val TYPE_BYTE_ARRAY = 1
        internal const val TYPE_BOOLEAN = 2
        internal const val TYPE_INT = 3
        internal const val TYPE_CHAR = 4
        internal const val TYPE_LONG = 5
        internal const val TYPE_FLOAT = 6
        internal const val TYPE_DOUBLE = 7
        internal const val TYPE_STRING = 8
    }

    private val out: DataOutputStream = DataOutputStream(outputStream).also {
        it.write(FILE_FORMAT_SIGNATURE)
        it.writeInt(FILE_FORMAT_VERSION)
    }

    fun writeByteArray(propName: String, propValue: ByteArray) {
        out.writeUTF(propName)
        out.writeByte(TYPE_BYTE_ARRAY)
        out.writeInt(propValue.size)
        out.write(propValue)
    }

    fun writeByteArray(propName: String, propValue: ByteArray, offset: Int, size: Int) {
        out.writeUTF(propName)
        out.writeByte(TYPE_BYTE_ARRAY)
        out.writeInt(size)
        out.write(propValue, offset, size)
    }

    fun writeBoolean(propName: String, propValue: Boolean) {
        out.writeUTF(propName)
        out.writeByte(TYPE_BOOLEAN)
        out.writeBoolean(propValue)
    }

    fun writeInt(propName: String, propValue: Int) {
        out.writeUTF(propName)
        out.writeByte(TYPE_INT)
        out.writeInt(propValue)
    }

    fun writeChar(propName: String, propValue: Char) {
        out.writeUTF(propName)
        out.writeByte(TYPE_CHAR)
        out.writeChar(propValue.toInt())
    }

    fun writeLong(propName: String, propValue: Long) {
        out.writeUTF(propName)
        out.writeByte(TYPE_LONG)
        out.writeLong(propValue)
    }

    fun writeFloat(propName: String, propValue: Float) {
        out.writeUTF(propName)
        out.writeByte(TYPE_FLOAT)
        out.writeFloat(propValue)
    }

    fun writeDouble(propName: String, propValue: Double) {
        out.writeUTF(propName)
        out.writeByte(TYPE_DOUBLE)
        out.writeDouble(propValue)
    }

    fun writeString(propName: String, propValue: String) {
        out.writeUTF(propName)
        out.writeByte(TYPE_STRING)
        out.writeUTF(propValue)
    }

    override fun flush() {
        out.flush()
    }

    override fun close() {
        @Suppress("ConvertTryFinallyToUseCall")
        try {
            flush()
        } finally {
            out.close()
        }
    }
}
