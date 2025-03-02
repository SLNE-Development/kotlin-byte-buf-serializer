package dev.slne.surf.bytebufserializer.internal

import dev.slne.surf.bytebufserializer.BufConfiguration
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
    private val configuration: BufConfiguration,
) : AbstractEncoder() {
    override fun encodeBoolean(value: Boolean) = useCustomOr(value) {
        buf.writeBoolean(value)
    }

    override fun encodeByte(value: Byte) = useCustomOr(value) {
        buf.writeByte(value.toInt())
    }

    override fun encodeShort(value: Short) = useCustomOr(value) {
        buf.writeShort(value.toInt())
    }

    override fun encodeInt(value: Int) = useCustomOr(value) {
        buf.writeInt(value)
    }

    override fun encodeLong(value: Long) = useCustomOr(value) {
        buf.writeLong(value)
    }

    override fun encodeFloat(value: Float) = useCustomOr(value) {
        buf.writeFloat(value)
    }

    override fun encodeDouble(value: Double) = useCustomOr(value) {
        buf.writeDouble(value)
    }

    override fun encodeChar(value: Char) = useCustomOr(value) {
        buf.writeChar(value.code)
    }

    override fun encodeString(value: String) = useCustomOr(value) {
        val bytes = value.toByteArray(Charsets.UTF_8)
        buf.writeInt(bytes.size)
        buf.writeBytes(bytes)
    }

    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) {
        if (configuration.enumsWithOrdinal) {
            encodeInt(index)
        } else {
            encodeString(enumDescriptor.getElementName(index))
        }
    }

    override fun beginCollection(
        descriptor: SerialDescriptor,
        collectionSize: Int
    ): CompositeEncoder {
        encodeInt(collectionSize)
        return this
    }

    override fun encodeNull() {
        encodeBoolean(false)
    }

    override fun encodeNotNullMark() {
        encodeBoolean(true)
    }

    private inline fun <reified T : Any> useCustomOr(value: T, fallback: () -> Unit) {
        val serializer = serializersModule.getContextual(T::class)

        if (serializer != null) {
            encodeSerializableValue(serializer, value)
        } else {
            fallback()
        }
    }
}