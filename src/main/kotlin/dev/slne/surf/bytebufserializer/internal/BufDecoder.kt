package dev.slne.surf.bytebufserializer.internal

import dev.slne.surf.bytebufserializer.BufConfiguration
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
    private val configuration: BufConfiguration,
    private var elementsCount: Int = 0
) : AbstractDecoder() {
    private var elementIndex = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (elementIndex == elementsCount) return CompositeDecoder.DECODE_DONE
        return elementIndex++
    }

    override fun decodeBoolean(): Boolean = useCustomOr {
        buf.readBoolean()
    }

    override fun decodeByte(): Byte = useCustomOr {
        buf.readByte()
    }

    override fun decodeShort(): Short = useCustomOr {
        buf.readShort()
    }

    override fun decodeInt(): Int = useCustomOr {
        buf.readInt()
    }

    override fun decodeLong(): Long = useCustomOr {
        buf.readLong()
    }

    override fun decodeFloat(): Float = useCustomOr {
        buf.readFloat()
    }

    override fun decodeDouble(): Double = useCustomOr {
        buf.readDouble()
    }

    override fun decodeChar(): Char = useCustomOr {
        buf.readChar().toChar()
    }


    override fun decodeString(): String = useCustomOr {
        val length = buf.readInt()
        val bytes = ByteArray(length)
        buf.readBytes(bytes)
        String(bytes, Charsets.UTF_8)
    }

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        return if (configuration.enumsWithOrdinal) {
            buf.readByte().toInt()
        } else {
            val name = decodeString()
            enumDescriptor.getElementIndex(name)
        }
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        return BufDecoder(buf, serializersModule, configuration, descriptor.elementsCount)
    }

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        decodeInt().also { elementsCount = it }

    override fun decodeSequentially(): Boolean = true
    override fun decodeNotNullMark(): Boolean = decodeBoolean()

    private inline fun <reified T : Any> useCustomOr(fallback: () -> T): T {
        val serializer = serializersModule.getContextual(T::class)

        return if (serializer != null) {
            decodeSerializableValue(serializer)
        } else {
            fallback()
        }
    }
}