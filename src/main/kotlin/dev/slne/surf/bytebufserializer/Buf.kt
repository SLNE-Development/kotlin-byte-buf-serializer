package dev.slne.surf.bytebufserializer

import dev.slne.surf.bytebufserializer.internal.BufEncoder
import dev.slne.surf.bytebufserializer.internal.BufDecoder
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import kotlinx.serialization.*
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

sealed class Buf(
    override val serializersModule: SerializersModule = EmptySerializersModule(),
    public val configuration: BufConfiguration = BufConfiguration()
) : BinaryFormat {

    companion object Default : Buf(EmptySerializersModule(), BufConfiguration()) {
        operator fun invoke(
            serializersModule: SerializersModule = EmptySerializersModule(),
            configurationBlock: BufConfiguration.() -> Unit = {}
        ): Buf {
            return BufImpl(serializersModule, BufConfiguration().apply(configurationBlock))
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> encodeToBuf(
        buf: ByteBuf,
        serializer: SerializationStrategy<T>,
        value: T
    ) {
        val encoder = BufEncoder(buf, serializersModule, configuration.enumsWithOrdinal)
        encoder.encodeSerializableValue(serializer, value)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> decodeFromBuf(
        buf: ByteBuf,
        deserializer: DeserializationStrategy<T>
    ): T {
        val decoder = BufDecoder(buf, serializersModule, configuration.enumsWithOrdinal)
        return decoder.decodeSerializableValue(deserializer)
    }

    override fun <T> encodeToByteArray(
        serializer: SerializationStrategy<T>,
        value: T
    ): ByteArray {
        val buf = Unpooled.buffer()
        encodeToBuf(buf, serializer, value)
        return ByteArray(buf.readableBytes()).also { buf.readBytes(it) }
    }

    override fun <T> decodeFromByteArray(
        deserializer: DeserializationStrategy<T>,
        bytes: ByteArray
    ): T {
        val buf = Unpooled.wrappedBuffer(bytes)
        return decodeFromBuf(buf, deserializer)
    }

    inline fun <reified T> encodeToByteArray(value: T): ByteArray {
        return encodeToByteArray(serializer(), value)
    }

    inline fun <reified T> decodeFromByteArray(bytes: ByteArray): T {
        return decodeFromByteArray(serializer(), bytes)
    }

    inline fun <reified T> encodeToBuf(buf: ByteBuf, value: T) {
        encodeToBuf(buf, serializer(), value)
    }

    inline fun <reified T> decodeFromBuf(buf: ByteBuf): T {
        return decodeFromBuf(buf, serializer())
    }
}

private class BufImpl(
    serializersModule: SerializersModule,
    configuration: BufConfiguration
) : Buf(serializersModule, configuration)