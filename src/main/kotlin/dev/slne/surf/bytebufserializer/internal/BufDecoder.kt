package dev.slne.surf.bytebufserializer.internal

import io.netty.buffer.ByteBuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class BufDecoder<B : ByteBuf>(
    val buf: B,
    override val serializersModule: SerializersModule,
    private val decodeEnumWithOrdinal: Boolean,
    private var elementsCount: Int = 0
) : AbstractDecoder() {
    private var elementIndex = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun decodeBoolean(): Boolean = buf.readBoolean()
    override fun decodeByte(): Byte = buf.readByte()
    override fun decodeShort(): Short = buf.readShort()
    override fun decodeInt(): Int = buf.readInt()
    override fun decodeLong(): Long = buf.readLong()
    override fun decodeFloat(): Float = buf.readFloat()
    override fun decodeDouble(): Double = buf.readDouble()
    override fun decodeChar(): Char = buf.readChar().toChar()


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

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return BufDecoder(buf, serializersModule, decodeEnumWithOrdinal, descriptor.elementsCount)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        buf.readInt().also { elementsCount = it }

    override fun decodeSequentially(): Boolean = true
    override fun decodeNotNullMark(): Boolean = decodeBoolean()
}