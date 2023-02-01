package com.testerum.common_fast_serialization.read_write

import java.io.DataInput
import java.io.DataInputStream
import java.io.EOFException
import java.io.InputStream
import java.util.TreeMap

@Suppress("NOTHING_TO_INLINE")
class FastInput private constructor(private val map: Map<String, Any>) {

    companion object {
        fun readFrom(inputStream: InputStream): FastInput {
            val map = HashMap<String, Any>()

            val input = DataInputStream(inputStream)

            input.validateFileFormat()

            while (true) {
                try {
                    val propName = input.readUTF()

                    @Suppress("MoveVariableDeclarationIntoWhen")
                    val propType = input.readByte().toInt()

                    val propValue = when (propType) {
                        FastOutput.TYPE_BYTE_ARRAY -> {
                            val size = input.readInt()
                            val byteArray = ByteArray(size)
                            input.read(byteArray)

                            byteArray
                        }
                        FastOutput.TYPE_BOOLEAN -> input.readBoolean()
                        FastOutput.TYPE_INT -> input.readInt()
                        FastOutput.TYPE_CHAR -> input.readChar()
                        FastOutput.TYPE_LONG -> input.readLong()
                        FastOutput.TYPE_FLOAT -> input.readFloat()
                        FastOutput.TYPE_DOUBLE -> input.readDouble()
                        FastOutput.TYPE_STRING -> input.readUTF()
                        else -> throw RuntimeException("unknown property type [$propType]")
                    }

                    map[propName] = propValue
                } catch (e: EOFException) {
                    break
                }
            }

            return FastInput(map)
        }

        private fun DataInput.validateFileFormat() {
            val actualFileFormatSignature = byteArrayOf(
                this.readByte(),
                this.readByte(),
                this.readByte(),
                this.readByte()
            )

            if (!actualFileFormatSignature.contentEquals(FastOutput.FILE_FORMAT_SIGNATURE)) {
                throw RuntimeException("invalid file format")
            }

            val actualFileFormatVersion = this.readInt()
            if (actualFileFormatVersion != FastOutput.FILE_FORMAT_VERSION) {
                throw RuntimeException(
                    "invalid file format version" +
                        ": expected ${FastOutput.FILE_FORMAT_VERSION}, but got $actualFileFormatVersion"
                )
            }
        }
    }

    fun asSortedMap(): Map<String, Any> {
        return TreeMap(map)
    }

    fun readByteArray(propName: String): ByteArray? = map[propName] as ByteArray?
    fun readBoolean(propName: String): Boolean? = map[propName] as Boolean?
    fun readInt(propName: String): Int? = map[propName] as Int?
    fun readChar(propName: String): Char? = map[propName] as Char?
    fun readLong(propName: String): Long? = map[propName] as Long?
    fun readFloat(propName: String): Float? = map[propName] as Float?
    fun readDouble(propName: String): Double? = map[propName] as Double?
    fun readString(propName: String): String? = map[propName] as String?

    inline fun readRequiredByteArray(propName: String): ByteArray = readByteArray(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredBoolean(propName: String): Boolean = readBoolean(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredInt(propName: String): Int = readInt(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredChar(propName: String): Char = readChar(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredLong(propName: String): Long = readLong(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredFloat(propName: String): Float = readFloat(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredDouble(propName: String): Double = readDouble(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    inline fun readRequiredString(propName: String): String = readString(propName)
        ?: throw IllegalArgumentException("cannot find property [$propName]")

    override fun toString(): String {
        return TreeMap(map)
            .entries
            .joinToString(separator = "\n") { (key, value) -> "$key -> $value" }
    }

}
