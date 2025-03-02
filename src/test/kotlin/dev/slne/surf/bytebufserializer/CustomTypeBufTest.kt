package dev.slne.surf.bytebufserializer

import io.netty.buffer.Unpooled
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.test.Test

class CustomTypeBufTest {

    private val bufFormat = Buf(SerializersModule {
        contextual(CustomTypeSerializer)
    })

    @Serializable
    data class Player(val name: String, val score: Int)

    data class CustomType(val value: Int)

    @Test
    fun `test encode and decode to ByteArray`() {
        val player = Player("Alice", 42)

        val bytes = bufFormat.encodeToByteArray(player)
        val decodedPlayer = bufFormat.decodeFromByteArray<Player>(bytes)

        assert(player == decodedPlayer)
    }

    @Test
    fun `test encode and decode to ByteBuf`() {
        val player = Player("Bob", 99)
        val buf = Unpooled.buffer()

        bufFormat.encodeToBuf(buf, player)
        val decodedPlayer = bufFormat.decodeFromBuf<Player>(buf)

        assert(player == decodedPlayer)
    }
}

object CustomTypeSerializer : KSerializer<CustomTypeBufTest.CustomType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("CustomTypeSerializer", PrimitiveKind.INT)

    override fun serialize(
        encoder: Encoder,
        value: CustomTypeBufTest.CustomType
    ) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): CustomTypeBufTest.CustomType {
        return CustomTypeBufTest.CustomType(decoder.decodeInt())
    }
}