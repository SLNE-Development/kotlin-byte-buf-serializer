@file:Suppress("UNCHECKED_CAST")

package dev.slne.surf.bytebufserializer

import dev.slne.surf.bytebufserializer.internal.BufDecoder
import dev.slne.surf.bytebufserializer.internal.BufEncoder
import io.netty.buffer.ByteBuf
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

@OptIn(ExperimentalSerializationApi::class)
interface KBufSerializer<T, B: ByteBuf>: KSerializer<T> {

    val bufClass: KClass<B>

    override fun serialize(encoder: Encoder, value: T) {
        require(encoder is BufEncoder<*>) { "This serializer can only be used with BufEncoder<$bufClass>" }

        val buf = encoder.buf
        require(bufClass.isInstance(buf)) {
            "Expected ByteBuf of type ${bufClass.qualifiedName}, but got ${buf::class.qualifiedName}"
        }

        serialize0(buf as B, value)
    }

    fun serialize0(buf: B, value: T)

    override fun deserialize(decoder: Decoder): T {
        require(decoder is BufDecoder<*>) { "This serializer can only be used with BufDecoder<$bufClass>" }

        val buf = decoder.buf
        require(bufClass.isInstance(buf)) {
            "Expected ByteBuf of type ${bufClass.qualifiedName}, but got ${buf::class.qualifiedName}"
        }

        return deserialize0(buf as B)
    }

    fun deserialize0(buf: B): T
}