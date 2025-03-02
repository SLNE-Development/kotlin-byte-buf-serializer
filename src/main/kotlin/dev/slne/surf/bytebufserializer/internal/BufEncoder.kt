package dev.slne.surf.bytebufserializer.internal

import io.netty.buffer.ByteBuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
class BufEncoder<B : ByteBuf>(
    val buf: B,
    override val serializersModule: SerializersModule,
    private val encodeEnumWithOrdinal: Boolean,
) : AbstractEncoder() {
    override fun encodeBoolean(value: Boolean) {
        buf.writeBoolean(value)
    }

    override fun encodeByte(value: Byte) {
        buf.writeByte(value.toInt())
    }

    override fun encodeShort(value: Short) {
        buf.writeShort(value.toInt())
    }

    override fun encodeInt(value: Int) {
        buf.writeInt(value)
    }

    override fun encodeLong(value: Long) {
        buf.writeLong(value)
    }

    override fun encodeFloat(value: Float) {
        buf.writeFloat(value)
    }

    override fun encodeDouble(value: Double) {
        buf.writeDouble(value)
    }

    override fun encodeChar(value: Char) {
        buf.writeChar(value.code)
    }

    override fun encodeString(value: String) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        buf.writeInt(bytes.size)
        buf.writeBytes(bytes)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        if (encodeEnumWithOrdinal) {
            encodeInt(index)
        } else {
            encodeString(enumDescriptor.getElementName(index))
        }
    }

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        buf.writeByte(index)
        return true
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        return BufEncoder(buf, serializersModule, encodeEnumWithOrdinal)
    }

    override fun endStructure(descriptor: SerialDescriptor) {
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        buf.writeInt(collectionSize)

        return return BufEncoder(buf, serializersModule, encodeEnumWithOrdinal)
    }


    override fun encodeNull() {
        buf.writeByte(-1)
    }
}