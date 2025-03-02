package dev.slne.surf.bytebufserializer.internal

import io.netty.buffer.ByteBuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class BufDecoder<B : ByteBuf>(
    private val buf: B,
    override val serializersModule: SerializersModule,
    private val decodeEnumWithOrdinal: Boolean,
) : AbstractDecoder() {

    private val readFields = mutableSetOf<Int>()

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (!buf.isReadable) return CompositeDecoder.DECODE_DONE

        val fieldId = buf.readByte().toInt()

        println("fieldId: $fieldId")
        println("descriptor.elementsCount: ${descriptor.elementsCount}")

        return if (fieldId in 0 until descriptor.elementsCount) {
            readFields.add(fieldId)
            fieldId
        } else {
            CompositeDecoder.UNKNOWN_NAME
        }
    }

    override fun decodeBoolean(): Boolean {
        return buf.readBoolean()
    }

    override fun decodeByte(): Byte {
        return buf.readByte()
    }

    override fun decodeShort(): Short {
        return buf.readShort()
    }

    override fun decodeInt(): Int {
        return buf.readInt()
    }

    override fun decodeLong(): Long {
        return buf.readLong()
    }

    override fun decodeFloat(): Float {
        return buf.readFloat()
    }

    override fun decodeDouble(): Double {
        return buf.readDouble()
    }

    override fun decodeChar(): Char {
        return buf.readChar().toChar()
    }

    override fun decodeString(): String {
        val length = buf.readInt()
        val bytes = ByteArray(length)
        buf.readBytes(bytes)
        return String(bytes, Charsets.UTF_8)
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return if (decodeEnumWithOrdinal) {
            buf.readByte().toInt()
        } else {
            val name = decodeString()
            enumDescriptor.getElementIndex(name)
        }
    }

    override fun decodeNotNullMark(): Boolean {
        return buf.readByte().toInt() != -1
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return BufDecoder(buf, serializersModule, decodeEnumWithOrdinal)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int {
        return buf.readInt()
    }
}